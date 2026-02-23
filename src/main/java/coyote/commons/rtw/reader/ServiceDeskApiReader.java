package coyote.commons.rtw.reader;

import com.sdcote.sdp.*;
import coyote.commons.DataFrameUtil;
import coyote.commons.StringUtil;
import coyote.commons.dataframe.DataField;
import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.DataFrameException;
import coyote.commons.log.Log;
import coyote.commons.rtw.ConfigTag;
import coyote.commons.rtw.context.TransactionContext;
import coyote.commons.rtw.context.TransformContext;
import coyote.commons.template.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads assets from ServiceDesk Plus.
 *
 * <p>This is placed in the {@code coyote.commons.rtw.reader} namespace to make
 * the configuration file look cleaner. RTW Jobs look in their own namespace by
 * default if the configuration attribute is not a fully-qualified name.</p>
 *
 * <p>Arguments currently supported:<ul>
 * <li>clientId - The OAuth client identifier to connect with the API.</li>
 * <li>clientSecret - The OAuth client secret to connect with the API.</li>
 * <li>clientToken - The OAuth refresh token to obtain access tokens for the client.</li>
 * <li>endpoint - The endpoint to add to the URL (e.g., /asset, /request, /cmdb, etc.)</li>
 * <li>resultField - The name of the results field (e.g., asset)</li>
 * <li>batch - how many records to read at a time.</li>
 * <li>flatten - determines if nested dataframes should be flattened to a single dataframe with dotted-name notation. (default=false)</li>
 * <li>limit - the maximum amount to read...useful during development.</li>
 * </ul>
 */
public class ServiceDeskApiReader extends AbstractFrameReader {
    /**
     * The current batch of records received.
     */
    private final List<DataFrame> currentPage = new ArrayList<>();
    /**
     * The query parameters for the API call.
     */
    private final ListInfo listInfo = new ListInfo();
    /**
     * The calculated size of the expected number of records to be returned with the present query.
     */
    private int resultSize = -1;
    /**
     * The current row index, what has been read by the reader (not the batch).
     */
    private int currentRow = 0;
    /**
     * The client credentials used for authenticating with the API endpoint.
     */
    private ClientCredentials clientCredentials = null;


    /**
     * All components are initialized through the {@code open(TransformContext)} method.
     *
     * <p>A last chack of the configuration parameters should be performed here
     * to make sure there are no issues or conflicts with other settings. For
     * example, setting that were present during configuration may have changed
     * at the time of initialization. If there are any issues during
     * initialization, simply place an error in the Transform context:
     * {@code context.setError("Initialization error");}</p>
     *
     * <p>Once all components have been initialized, the engine will start
     * running, unless there is an error in the Transform context.</p>
     *
     * @param context The transform context all components share.
     */
    @Override
    public void open(TransformContext context) {
        super.open(context);
        String id = Template.resolve(configuration.getString("clientid"), context.getSymbols());
        String secret = Template.resolve(configuration.getString("clientsecret"), context.getSymbols());
        String token = Template.resolve(configuration.getString("clienttoken"), context.getSymbols());
        clientCredentials = new ClientCredentials(id, secret, token);

        // Set our batch size (ListInfo.rowCount)
        if (configuration.containsIgnoreCase(ConfigTag.BATCH)) {
            int batchSize = getInteger(ConfigTag.BATCH);
            if (batchSize > 0) {
                if (getReadLimit() > 0 && batchSize > getReadLimit()) listInfo.setRowCount((int) getReadLimit());
                else listInfo.setRowCount(batchSize);
            }
        }

        if (!configuration.containsIgnoreCase(ConfigTag.ENDPOINT)) {
            context.setError(getClass().getSimpleName() + ": Required attribute '" + ConfigTag.ENDPOINT + "' is missing");
            return;
        }

        if (!configuration.containsIgnoreCase(SDP.RESULTS_FIELD_TAG)) {
            context.setError(getClass().getSimpleName() + ": Required attribute '" + SDP.RESULTS_FIELD_TAG + "' is missing");
            return;
        }

        // If there is a search criteria field, use the JSON to create search criteria and add it to the list info
        if (configuration.containsIgnoreCase(SDP.SEARCH_CRITERIA_TAG)) {
            String json = configuration.getString(SDP.SEARCH_CRITERIA_TAG);
            try {
                SearchCriteria searchCriteria = new SearchCriteria(Template.resolve(json, context.getSymbols()));
                listInfo.setSearchCriteria(searchCriteria);
            } catch (IllegalArgumentException e) {
                context.setError(getClass().getSimpleName() + ": Invalid search criteria: " + e.getMessage());
                return;
            }
        }

        if(configuration.containsIgnoreCase(SDP.FIELDS_REQUIRED_TAG)) {
            DataField field = configuration.getFieldIgnoreCase(SDP.FIELDS_REQUIRED_TAG);
            if(field != null && field.isFrame()) {
                List<String> requiredFields = new ArrayList<>();
                for(DataField dataField: ((DataFrame)field.getObjectValue()).getFields()) {
                    requiredFields.add(dataField.getStringValue());
                }
                if( !requiredFields.isEmpty() ) {
                    listInfo.setFieldsRequired(requiredFields.toArray(new String[0]));
                }
            }
        }

        // Defaults, maybe make configurable if there is benefit to do so
        listInfo.setSortField("name");
        listInfo.setSortOrder(ListInfo.ASCENDING);

    }


    /**
     * Read and return a frame.
     *
     * <p>It is possible to return a null frame. This may be caused by a timeout
     * waiting for data to arrive. In such cases, the engine will continue the
     * loop, skipping any frame processing. The loop will only be exited if the
     * transform context is in error or if the reader returns true on the call
     * to check {@code eof()}.
     *
     * @param context the context containing data related to the current transaction.
     * @return the dataframe containing the data record, null if no record was read
     */
    @Override
    public DataFrame read(TransactionContext context) {
        // If we have no idea how many to expect OR are not at EOF
        if (resultSize < 0 || currentRow <= resultSize) {

            // if there is no data in the current page
            if (currentPage.isEmpty()) {
                // load the next page of data
                nextPage(context);
            }

            // if we still have no records...assume a premature EOF condition
            if (currentPage.isEmpty()) {
                if (StringUtil.isNotBlank(context.getErrorMessage())) Log.error(context.getErrorMessage());
                context.setError("Unexpected end of data: expected " + resultSize + " read in " + currentRow);

                // make sure we register as EOF
                resultSize = currentRow;
                return null;
            }

            // increment the current row
            currentRow++;

            // if the current row matches the expected result size, flag this as the
            // last frame in the transaction context
            if ((currentRow == resultSize) || (getReadLimit() > 0 && currentRow == getReadLimit())) {
                context.setLastFrame(true);
            }

            // take the next record from the top of the page
            return currentPage.remove(0);

        } // !eof

        // support TransactionContext.setLastFrame( true )
        if (this.resultSize >= 0 && resultSize == currentRow) {
            context.setLastFrame(true);
        }

        return null;
    }


    /**
     * Retrieve the next batch of records into our buffer.
     */
    private void nextPage(TransactionContext context) {
        listInfo.setStartIndex(currentRow);
        Log.trace(String.format("loading page - %s", listInfo));

        try {
            // Get the next batch of records
            ApiResponse response = SDP.callApi(clientCredentials, getEndPoint(), listInfo, getResultsField());

            // add them to the current page
            for (final DataFrame frame : response.getResults()) {
                if (isFlattening()) currentPage.add(DataFrameUtil.flatten(frame));
                else currentPage.add(frame);
            }

            // Try to detect the result size. NOTE: This is not foolproof, we may
            // have coincidentally hit a natural multiple of our page size (e.g.  the
            // limit or batch size parameter)
            Log.debug(String.format("ResultSize:%d - CurrentPageSize:%d - Batch:%d", resultSize, currentPage.size(), listInfo.getRowCount()));

            // if we are paging through data
            if (listInfo.getRowCount() > 0) {
                // If we don't know how many records to expect and the next batch did
                // not return the full batch size, assume we have reached the max
                // result size.
                if (resultSize < 0 && (currentPage.size() < listInfo.getRowCount())) {
                    Log.debug("Batch read less than expected assuming result size to be " + (currentRow + currentPage.size()));
                    resultSize = (currentRow + currentPage.size());
                }
            } else {
                // Since we are trying to read everything at once, if we retrieved less
                // than the limit, assume we have reached the max result size.
                if (currentPage.size() < getBatch()) {
                    Log.debug("Expecting result size to be " + (currentRow + currentPage.size()));
                    resultSize = (currentRow + currentPage.size());
                }

            }

        } catch (Exception e) {
            context.setError("The Reader could not query the instance: " + e.getMessage());
            context.setState("Read Error");
        }

    }


    /**
     * @return the name of the field that contains our query results.
     */
    private String getResultsField() {
        return configuration.getString(SDP.RESULTS_FIELD_TAG);
    }


    /**
     * @return the endpoint path to add to the request URI to make our call
     */
    private String getEndPoint() {
        return configuration.getString(ConfigTag.ENDPOINT);
    }


    /**
     * @return true if the reader is to flatten data into dotted-name notation,
     * false (default) to keep the dataframe nesting.
     */
    private boolean isFlattening() {
        if (configuration.containsIgnoreCase(ConfigTag.FLATTEN)) {
            return configuration.getBoolean(ConfigTag.FLATTEN);
        } else return false;
    }


    /**
     * @return the number of records to be retrieved at a time from the source.
     */
    public int getBatch() {
        try {
            return configuration.getAsInt(ConfigTag.BATCH);
        } catch (DataFrameException e) {
            return 0;
        }
    }


    /**
     * @return true if there are no more records to be read, false to keep
     * reading.
     */
    @Override
    public boolean eof() {
        if (resultSize >= 0)
            return currentRow >= resultSize;
        else
            return getReadLimit() > 0 && currentRow >= getReadLimit();
    }


    /**
     * Terminate processing and clean up resources.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        Log.debug("Closing -------------------------------------------");
        super.close();
    }

}

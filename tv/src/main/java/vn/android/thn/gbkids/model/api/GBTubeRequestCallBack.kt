package vn.android.thn.gbkids.model.api

import vn.android.thn.gbkids.model.api.request.GBTubeRequest
import vn.android.thn.gbkids.model.api.response.GBTubeResponse
import vn.android.thn.library.net.GBRequestError

interface GBTubeRequestCallBack {
    /**
     *
     * @param response
     * @param objRequest
     */
    fun  onResponse(httpCode: Int, response: GBTubeResponse, request: GBTubeRequest)

    /**
     *
     * @param errorRequest
     * @param objRequest
     */
    fun  onRequestError(errorRequest: GBRequestError, request: GBTubeRequest)
}

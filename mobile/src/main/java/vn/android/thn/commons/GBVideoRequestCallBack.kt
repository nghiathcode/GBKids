package vn.android.thn.gbkids.model.api

import vn.android.thn.commons.GBVideoRequest
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.library.net.GBRequestError

interface GBVideoRequestCallBack {
    /**
     *
     * @param response
     * @param objRequest
     */
    fun  onResponse(httpCode: Int, response: GBTubeResponse, request: GBVideoRequest)

    /**
     *
     * @param errorRequest
     * @param objRequest
     */
    fun  onRequestError(errorRequest: GBRequestError, request: GBVideoRequest)
}

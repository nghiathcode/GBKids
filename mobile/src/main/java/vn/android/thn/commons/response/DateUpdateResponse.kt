package vn.android.thn.commons.response

import org.json.JSONObject

class DateUpdateResponse:GBTubeResponse() {
    var dateUpdate = ""
    override fun onJsonData(data: JSONObject) {
        if (has(data,"dateUpdate")){
            dateUpdate = data.getString("dateUpdate")
        }
    }
}
package vn.android.thn.gbkids.model.response

import org.json.JSONObject
import vn.android.thn.commons.response.GBTubeResponse
import vn.android.thn.gbkids.model.SettingEntity

class SettingResponse : GBTubeResponse() {
    var setting = SettingEntity()
    override fun onJsonData(data: JSONObject) {
        setting = app.gson.fromJson<SettingEntity>(data.toString(),SettingEntity::class.java)
    }
}
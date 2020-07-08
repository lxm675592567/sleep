package com.hife.service;

import com.alibaba.fastjson.JSONObject;
import org.json.JSONException;

import java.text.ParseException;
import java.util.List;

public interface EdfService {

    String getWave(JSONObject jsonObject) throws ParseException;

    String getDatWave(JSONObject jsonObject) throws ParseException;

    List getFileName();

    List getFileDatName();

    String getDeepSleepWave(JSONObject jsonObject) throws ParseException;

    JSONObject getSleepBasicValue(JSONObject jsonObject) throws ParseException, JSONException;

    JSONObject getSleepDataMap(JSONObject jsonObject) throws ParseException, JSONException;
}


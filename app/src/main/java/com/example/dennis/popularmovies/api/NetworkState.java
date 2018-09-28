package com.example.dennis.popularmovies.api;

/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class NetworkState {
    private final String status;
    private final String msg;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;


    public NetworkState(String status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED=new NetworkState("LOADED","Success");
        LOADING=new NetworkState("RUNNING","Running");

    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}

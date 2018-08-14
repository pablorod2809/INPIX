/*
 * Copyright (C) 2009 The Android Open Source Project
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

/*
 * Modified by Nilesh Patel
 */

package com.lightbox.android.inpix.device;

import android.os.Build;

public class SAMSUNGGingerbreadCameraHolder extends GingerbreadCameraHolder {
    private static final String TAG = "SAMSUNGGingerbreadCameraHolder";

    @Override
    public int getCameraOrientation(int cameraId, int orientationSensorValue) {
        return mInfo[cameraId].orientation;
    }

    @Override
    public int getAdditionalRotation(int cameraId, int orientationSensorValue) {
        int rsta;
        if (isFrontFacing(cameraId)&& Build.MODEL.equals("GT-I9190")) {
            rsta = 270;
        }else{
            rsta = 90;
        }
        return rsta;
    }

}

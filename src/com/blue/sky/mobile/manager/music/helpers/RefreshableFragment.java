/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.blue.sky.mobile.manager.music.helpers;

import android.support.v4.app.Fragment;
import com.blue.sky.common.fragment.BaseFragment;


/**
 * An abstract class that defines a {@link android.support.v4.app.Fragment} like refreshable
 */
public abstract class RefreshableFragment extends BaseFragment {

    /**
     * Method invoked when the fragment need to be refreshed
     */
    public abstract void refresh();
}

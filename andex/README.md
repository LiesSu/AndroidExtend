## SharedPreferences拓展类

* 整合ContentProvider与SharedPreferences。
* 使用ContentProvider/ContentResolver作为数据共享的载体，弥补了SharedPreference极度暴力且进程不安全的数据共享方式。
* 一个server端，多个client端。随性共享。
* 在Client端使用，除去实例化步骤外几乎与SharedPreferences相差无几。
* 旨在共享简单的键值对数据，便捷简单。（如果需要共享复杂数据，请使用Android标准数据共享方式）


### Introduction
1. 在需要实现数据共享的module中，引入依赖包；
2. 选择一个进程或者module作为数据共享server端；
3. 在server端对应module的AndroidManifest.xml加入以下代码，注册ContentProvider组件：
```
<provider
            android:authorities="com.liessu.andex.sharedmulti.SharedPreferencesProvider"
            android:name="com.liessu.andex.sharedmulti.SharedPreferencesProvider"
            android:exported="true"/>
```
4. 在server端直接使用SharedPreferences进行数据操作即可：
```
SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                            SharedPreferencesProvider.SHARED_FILE_NAME, Context.MODE_PRIVATE);
```
5. 在Client端实例化SharedPreferences时，使用SharedPreferencesResolver实例化便可以轻松地实现与server端数据的共享：
```
//SharedPreferencesResolver实现了SharedPreferences大部分接口，直接按照你的使用习惯使用即可
SharedPreferencesResolver sharedPreferences = new SharedPreferencesResolver(context);
```
6. 如果需要订阅SharedPreferences内的数据变化，做法与普通SharedPreferences别无二致：
```
@Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
        Log.d( TAG , "onSharedPreferenceChanged is called");
        //do something
    }
```


### Download
使用Gradle引入即可：
```
compile 'com.liessu.andex:andex:1.0.1'
```

### License
```
Copyright 2013 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
```

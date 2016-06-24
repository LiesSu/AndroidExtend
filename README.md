# AndroidExtender
Extensional class of Android .


## SharedPreferences��չ��

* ����ContentProvider��SharedPreferences��
* ʹ��ContentProvider/ContentResolver��Ϊ���ݹ�������壬�ֲ���SharedPreference���ȱ����ҽ��̲���ȫ�����ݹ���ʽ��
* һ��server�ˣ����client�ˡ����Թ���
* ��Client��ʹ�ã���ȥʵ���������⼸����SharedPreferences����޼���
* ּ�ڹ���򵥵ļ�ֵ�����ݣ���ݼ򵥡��������Ҫ���������ݣ���ʹ��Android��׼���ݹ���ʽ��


### Introduction
1. ����Ҫʵ�����ݹ����module�У�������������
2. ѡ��һ�����̻���module��Ϊ���ݹ���server�ˣ�
3. ��server�˶�Ӧmodule��AndroidManifest.xml�������´��룬ע��ContentProvider�����
```
<provider
            android:authorities="com.liessu.andex.sharedmulti.SharedPreferencesProvider"
            android:name="com.liessu.andex.sharedmulti.SharedPreferencesProvider"
            android:exported="true"/>
```

4. ��server��ֱ��ʹ��SharedPreferences�������ݲ������ɣ�
```
SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                            SharedPreferencesProvider.SHARED_FILE_NAME, Context.MODE_PRIVATE);
```

5. ��Client��ʵ����SharedPreferencesʱ��ʹ��SharedPreferencesResolverʵ������������ɵ�ʵ����server�����ݵĹ���
```
//SharedPreferencesResolverʵ����SharedPreferences�󲿷ֽӿڣ�ֱ�Ӱ������ʹ��ϰ��ʹ�ü���
SharedPreferencesResolver sharedPreferences = new SharedPreferencesResolver(context);
```

6. �����Ҫ����SharedPreferences�ڵ����ݱ仯����������ͨSharedPreferences���޶��£�
```
@Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
        Log.d( TAG , "onSharedPreferenceChanged is called");
        //do something
    }
```


### Download
ʹ��Gradle���뼴�ɣ�
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


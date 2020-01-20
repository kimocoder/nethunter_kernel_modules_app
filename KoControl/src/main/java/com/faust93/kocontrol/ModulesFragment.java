package com.faust93.kocontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Created by faust93 on 09.08.13.
 */
public class ModulesFragment extends Fragment implements Constants {

    private LinearLayout mModulesView;
    private File[] modules;
    private SharedPreferences preferences;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);

        View myView = inflater.inflate(R.layout.fragment_modules, container, false);
        mModulesView = myView.findViewById(R.id.ui_modules_view);

        Switch enabledSwitch = myView.findViewById(R.id.ui_enabled_switch);
        enabledSwitch.setChecked(preferences.getBoolean("onBoot", false));
        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
             public void onCheckedChanged(CompoundButton cbBox, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if ( isChecked )
                    editor.putBoolean("onBoot", true);
                     else
                    editor.putBoolean("onBoot", false);
                editor.apply();
            }
        });

        try {
            updateView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myView;
    }

    public void updateView() throws Exception {

       // mModulesView.removeAllViews();
        File dir = new File(MOD_DIR);
        modules = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".ko");
            }
        });

        int i = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            for(File file: Objects.requireNonNull(modules)){
              genModuleRow(file.getName(), mModulesView,i++);
            }
        }
    }

    private void genModuleRow(String name, ViewGroup parent, int index) throws Exception {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.module_row, parent, false);

        TextView moduleName = view.findViewById(R.id.ui_module_name);
        CheckBox atBoot = view.findViewById(R.id.ui_at_boot);
        Switch stateSwitch = view.findViewById(R.id.ui_module_switch);

        moduleName.setText(name);
        stateSwitch.setId(index);

        atBoot.setChecked(preferences.getBoolean(name, false));
        atBoot.setId(index);

        atBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton cbBox, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                if (isChecked) {
                    editor.putBoolean(modules[cbBox.getId()].getName(), true);
                    Toast.makeText(getActivity(), "Module " + modules[cbBox.getId()].getName() + " will be loaded at next boot!", Toast.LENGTH_SHORT).show();
                } else {
                    editor.putBoolean(modules[cbBox.getId()].getName(), false);
                }
                    editor.apply();
            }
        });

        if(checkModule(name.substring(0, name.indexOf('.')))) {
            stateSwitch.setChecked(true);
        } else  {
            stateSwitch.setChecked(false);
        }

        stateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton sSwitch, boolean isChecked) {
                if(isChecked){
                    if(!Utils.spawnCmd("insmod " + modules[sSwitch.getId()].getAbsolutePath()))
                        Toast.makeText(getActivity(), "Module " + modules[sSwitch.getId()].getName() + " inserted" ,Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(getActivity(), "Module " + modules[sSwitch.getId()].getName() + " load error" ,Toast.LENGTH_SHORT).show();
                        sSwitch.setChecked(false);
                    }
                } else {
                    if(!Utils.spawnCmd("rmmod " + modules[sSwitch.getId()].getName()))
                        Toast.makeText(getActivity(), "Module " + modules[sSwitch.getId()].getName() + " unloaded" ,Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(getActivity(), "Module " + modules[sSwitch.getId()].getName() + " unload error" ,Toast.LENGTH_SHORT).show();
                        sSwitch.setChecked(true);
                    }
                }
            }
        });
        parent.addView(view);

    }

    /* do check if module already loaded */
    private boolean checkModule(String moduleName) throws Exception {

        Process process;

        Log.d(APP_TAG, "checkModule() entry: " + moduleName);

        process = Runtime.getRuntime().exec("lsmod");

        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            Log.d(APP_TAG, "Line: " + line);
            if (line.contains(moduleName.replace("-","_"))) {
                return true;
            }
        }
        in.close();
        process.waitFor();
        return false;
    }

}

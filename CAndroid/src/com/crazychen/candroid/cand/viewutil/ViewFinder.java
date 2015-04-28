package com.crazychen.candroid.cand.viewutil;

import android.app.Activity;
import android.view.View;

public class ViewFinder {

    private View view;
    private Activity activity;  

    public ViewFinder(View view) {
        this.view = view;
    }

    public ViewFinder(Activity activity) {
        this.activity = activity;
    }
    public View findViewById(int id) {
        return activity == null ? view.findViewById(id) : activity.findViewById(id);
    }   

    public View findViewByInfo(ViewInjectInfo info) {
        return findViewById((Integer) info.value, info.parentId);
    }
    
    public View findViewById(int id, int pid) {
        View pView = null;
        if (pid > 0) {
            pView = this.findViewById(pid);
        }

        View view = null;
        if (pView != null) {
            view = pView.findViewById(id);
        } else {
            view = this.findViewById(id);
        }
        return view;
    }    
}

package com.tubiapp.demochatxmpp.abstracts;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by Binh CAO on 6/13/2015.
 */
public class BaseArrayAdapter<T> extends ArrayAdapter<T> {

    protected int selectedCell = 0;

    public BaseArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        //initInflater(context);
    }

    public BaseArrayAdapter(Context context, List<T> objects) {
        super(context, 0, objects);
        //initInflater(context);
    }

    /*private void initInflater(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }*/

    public int getSelectedCell() {
        return selectedCell;
    }

    public void setSelectedCell(int selected) {
        this.selectedCell = selected;
        this.notifyDataSetChanged();
    }

    public void updateItemAtPosition(ListView listView, int position) {
        int visiblePosition = listView.getFirstVisiblePosition();
        View view = listView.getChildAt(position - visiblePosition);
        listView.getAdapter().getView(position, view, listView);
    }
}

package com.example.electronicqueue.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.electronicqueue.server.MyBundle;

import java.util.List;

public class AdapterListTicket extends BaseAdapter {

    private Context mContext;
    private List<MyBundle> mData;
    private LayoutInflater layoutInflater;

    public AdapterListTicket(Context Context, List<MyBundle> Data) {
        this.mContext = Context;
        this.mData = Data;
        layoutInflater = (LayoutInflater) mContext.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
    }

    class ViewHolder {
        TextView textViewNumberTicket;
        TextView textViewNumberWindow;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_view_ticket_list, parent, false);
            holder = new ViewHolder();
            holder.textViewNumberTicket = (TextView) view.findViewById(R.id.textViewTicketListTicket);
            holder.textViewNumberWindow = (TextView) view.findViewById(R.id.textViewTicketListWindow);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MyBundle myBundle = (MyBundle) getItem(position);

        holder.textViewNumberTicket.setText("Number you ticket: " + String.valueOf(myBundle.getNumberTicket()));
        holder.textViewNumberWindow.setText("Number you window: " + String.valueOf(myBundle.getNumberWindow()));

        return view;
    }
}

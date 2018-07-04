package com.reveautomation.revesmartsecuritykit_online.example1_scanning;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.scan.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class ScanResultsAdapter extends RecyclerView.Adapter<ScanResultsAdapter.ViewHolder> {
    DashboardActivity sa = null;
    String chk="";

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView line1;
        @BindView(android.R.id.text2)
        TextView line2;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    interface OnAdapterItemClickListener {

        void onAdapterViewClick(View view);
    }

    private static final Comparator<ScanResult> SORTING_COMPARATOR = (lhs, rhs) ->
            lhs.getBleDevice().getMacAddress().compareTo(rhs.getBleDevice().getMacAddress());
    private final List<ScanResult> data = new ArrayList<>();
    private OnAdapterItemClickListener onAdapterItemClickListener;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onAdapterItemClickListener != null) {
                onAdapterItemClickListener.onAdapterViewClick(v);
            }
        }
    };

    void addScanResult(ScanResult bleScanResult) {
        // Not the best way to ensure distinct devices, just for sake on the demo.

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getBleDevice().equals(bleScanResult.getBleDevice())) {

            }
            return;
        }
        data.add(bleScanResult);
        Collections.sort(data, SORTING_COMPARATOR);
        notifyDataSetChanged();
    }

    void clearScanResults() {
        data.clear();
        notifyDataSetChanged();
    }

    ScanResult getItemAtPosition(int childAdapterPosition) {
        return data.get(childAdapterPosition);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ScanResult rxBleScanResult = data.get(position);
        final RxBleDevice bleDevice = rxBleScanResult.getBleDevice();
        holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
        //holder.line2.setText(String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.getRssi()));
        //holder.line2.setText(String.format(Locale.getDefault(), "RSSI: %s", HexString.bytesToHex(rxBleScanResult.getScanRecord().getBytes()).trim()));

       // Log.d("chkkkkkkkkkkk   ",chk);

        if (sa != null){
            //sa.updateEvent(1);
            if (chk.contains("02010617FFF9FF14FFB20201A0061F064")){
                // Door Close

                holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
                holder.line2.setText(String.format(Locale.getDefault(),"%s", "DOOR Closed"));



                //  sa.updateEvent(1);
            }else if (chk.contains("02010617FFF9FF14FFB20201A0061E064")){
                //Door open
               // sa.updateEvent(1);
                holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
                holder.line2.setText(String.format(Locale.getDefault(),"%s", "DOOR Open"));


            }else if (chk.contains("02010617FFF9FF14FFB20201B0031F064")){
                //Pir not Detect
               // sa.updateEvent(3);
                holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
                holder.line2.setText(String.format(Locale.getDefault(),"%s", "PIR Not-Detect"));
            }else if (chk.contains("02010617FFF9FF14FFB20201B0031E064")){
                //Pir Detected
              //  sa.updateEvent(4);
                holder.line1.setText(String.format(Locale.getDefault(), "%s (%s)", bleDevice.getMacAddress(), bleDevice.getName()));
                holder.line2.setText(String.format(Locale.getDefault(),"%s", "PIR Detect"));

            }

        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.two_line_list_item, parent, false);
        itemView.setOnClickListener(onClickListener);
        sa = new DashboardActivity();
        return new ViewHolder(itemView);
    }

    void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }
}

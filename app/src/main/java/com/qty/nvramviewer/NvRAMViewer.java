package com.qty.nvramviewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class NvRAMViewer extends Activity {

    private TextView mEmptyView;
    private LinearLayout mContentContainer;
    private ListView mListView;
    private View mWriteNvDialogView;
    private TextView mWriteNvTipTv;
    private EditText mWriteNvDataEt;
    private EditText mWriteNvDataLengthEt;
    private EditText mWriteStartSubscriptEt;
    private View mEraseNvDialogView;
    private TextView mEraseNvTipTv;
    private EditText mEraseStartSubscriptEt;
    private EditText mEraseEndSubscriptEt;
    private View mListViewFooterView;
    private ListViewAdapter mAdapter;
    private Dialog mWriteNvDialog;
    private Dialog mEraseNvDialog;

    private int mNvLength = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nv_ramviewer);

        mEmptyView = (TextView) findViewById(R.id.empty);
        mContentContainer = (LinearLayout) findViewById(R.id.activity_nv_ramviewer);
        mListView = (ListView) findViewById(R.id.list);
        mWriteNvDialogView = getLayoutInflater().inflate(R.layout.write_nv_dialog_layout, null, false);
        mWriteNvTipTv = (TextView) mWriteNvDialogView.findViewById(R.id.tip);
        mWriteNvDataEt = (EditText) mWriteNvDialogView.findViewById(R.id.data);
        mWriteNvDataLengthEt = (EditText) mWriteNvDialogView.findViewById(R.id.data_length);
        mWriteStartSubscriptEt = (EditText) mWriteNvDialogView.findViewById(R.id.start_subscript);
        mEraseNvDialogView = getLayoutInflater().inflate(R.layout.erase_nv_dialog_layout, null, false);
        mEraseNvTipTv = (TextView) mEraseNvDialogView.findViewById(R.id.tip);
        mEraseStartSubscriptEt = (EditText) mEraseNvDialogView.findViewById(R.id.start_subscript);
        mEraseEndSubscriptEt = (EditText) mEraseNvDialogView.findViewById(R.id.end_subscript);
        mListViewFooterView = getLayoutInflater().inflate(R.layout.list_view_footer_view, mListView, false);
        mListView.addFooterView(mListViewFooterView);
        mAdapter = new ListViewAdapter(this, getNvRamValues());
        mListView.setAdapter(mAdapter);

        try {
            mNvLength = NvRAMUtils.getNvRAMLength();
        } catch (RemoteException e) {
            Log.e(this, "onCreate=>error: ", e);
            mNvLength = 2048;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nvram_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;

            case R.id.write:
                writeNv();
                break;

            case R.id.erase:
                eraseNv();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private ArrayList<ListItem> getNvRamValues() {
        ArrayList<ListItem> list = new ArrayList<ListItem>();
        try {
            byte[] bytes = NvRAMUtils.readNV();
            if (bytes != null) {
                int half = bytes.length / 2;
                if (bytes.length % 2 != 0) {
                    half++;
                }
                Log.d(this, "getNvRamValues=>half: " + half + " mod: " + (bytes.length % 2));
                for (int i = 0, j = half; i < half && j < bytes.length; i++, j++) {
                    ListItem item = new ListItem(i + "", bytes[i] + "",
                            new String(new byte[]{bytes[i]}), j + "", bytes[j] + "", new String(new byte[]{bytes[j]}));
                    list.add(item);
                }
            }
        } catch (RemoteException e) {
            Log.e(this, "getNvRamValues=>error: ", e);
            Toast.makeText(this, R.string.read_nv_fail, Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    private void refresh() {
        ArrayList<ListItem> list = getNvRamValues();
        if (list == null || list.size() <= 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mContentContainer.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
        }
        mAdapter.setList(list);
    }

    private void writeNv() {
        if (mWriteNvDialog == null) {
            mWriteNvDialog = createWriteNvDialog();
        }
        if (!mWriteNvDialog.isShowing()) {
            mWriteNvDialog.show();
        }
    }

    private void eraseNv() {
        if (mEraseNvDialog == null) {
            mEraseNvDialog = createEraseNvDialog();
        }
        if (!mEraseNvDialog.isShowing()) {
            mEraseNvDialog.show();
        }
    }

    private Dialog createWriteNvDialog() {
        mWriteNvTipTv.setText(getString(R.string.dialog_nv_info_tip, mNvLength));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.write_nv_dialog_title)
                .setView(mWriteNvDialogView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (writeDataToNv()) {
                            Toast.makeText(NvRAMViewer.this, R.string.write_nv_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NvRAMViewer.this, R.string.write_nv_fail, Toast.LENGTH_SHORT).show();
                        }
                        refresh();
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private Dialog createEraseNvDialog() {
        mEraseNvTipTv.setText(getString(R.string.dialog_nv_info_tip, mNvLength));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.erase_nv_dialog_title)
                .setView(mEraseNvDialogView)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (eraseSpecifyNv()) {
                            Toast.makeText(NvRAMViewer.this, R.string.erase_nv_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(NvRAMViewer.this, R.string.erase_nv_fail, Toast.LENGTH_SHORT).show();
                        }
                        refresh();
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private boolean writeDataToNv() {
        boolean result = false;
        String data = mWriteNvDataEt.getText().toString();
        String lengthStr = mWriteNvDataLengthEt.getText().toString().trim();
        String indexStr = mWriteStartSubscriptEt.getText().toString().trim();
        Log.d(this, "writeDataToNv=>data: " + data + " length: " + lengthStr + " indexStr: " + indexStr);
        try {
            int length = Integer.parseInt(lengthStr);
            int index = Integer.parseInt(indexStr);
            int maxLength = NvRAMUtils.getNvRAMLength();
            if (data.length() > 0) {
                if (index >= 0 && index + length < maxLength) {
                    if (data.length() > length) {
                        data = data.substring(0, length);
                    }
                    byte[] bytes = getDataBytes(data, length);
                    result = NvRAMUtils.writeNV(index, bytes);
                }
            }
        } catch (Exception e) {
            Log.e(this, "writeDataToNv=>error: ", e);
        }
        Log.d(this, "writeDataToNv=>result: " + result);
        return result;
    }

    private byte[] getDataBytes(String str, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < str.length(); i++) {
            bytes[i] = (byte) str.charAt(i);
        }
        return bytes;
    }

    private boolean eraseSpecifyNv() {
        boolean result = false;
        String startIndexStr = mEraseStartSubscriptEt.getText().toString().trim();
        String endIndexStr = mEraseEndSubscriptEt.getText().toString().trim();
        Log.d(this, "eraseSpecifyNv=>startIndex: " + startIndexStr + " endIndex: " + endIndexStr);
        try {
            int maxLength = NvRAMUtils.getNvRAMLength();
            int startIndex = Integer.parseInt(startIndexStr);
            int endIndex = Integer.parseInt(endIndexStr);
            if (endIndex > startIndex) {
                if (startIndex >= 0 && endIndex <= maxLength) {
                    int len = endIndex - startIndex;
                    byte[] bytes = new byte[len];
                    result = NvRAMUtils.writeNV(startIndex, bytes);
                }
            }
        } catch (Exception e) {
            Log.d(this, "eraseSpecifyNv=>error: ", e);
        }
        Log.d(this, "eraseSpecifyNv=>result: " + result);
        return result;
    }

    class ListViewAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;
        private ArrayList<ListItem> mList;

        public ListViewAdapter(Context context, ArrayList<ListItem> list) {
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mList = list;
        }

        public void setList(ArrayList<ListItem> list) {
            if (list != null) {
                mList = list;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public ListItem getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, parent, false);
                holder = new ViewHolder();
                holder.mIndexTv1 = (TextView) convertView.findViewById(R.id.index1);
                holder.mValueTv1 = (TextView) convertView.findViewById(R.id.value1);
                holder.mCharTv1 = (TextView) convertView.findViewById(R.id.character1);
                holder.mIndexTv2 = (TextView) convertView.findViewById(R.id.index2);
                holder.mValueTv2 = (TextView) convertView.findViewById(R.id.value2);
                holder.mCharTv2 = (TextView) convertView.findViewById(R.id.character2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ListItem item = mList.get(position);
            holder.mIndexTv1.setText(item.mIndex1);
            holder.mValueTv1.setText(item.mValue1);
            holder.mCharTv1.setText(item.mChar1);
            holder.mIndexTv2.setText(item.mIndex2);
            holder.mValueTv2.setText(item.mValue2);
            holder.mCharTv2.setText(item.mChar2);
            return convertView;
        }

        class ViewHolder {
            TextView mIndexTv1;
            TextView mValueTv1;
            TextView mCharTv1;
            TextView mIndexTv2;
            TextView mValueTv2;
            TextView mCharTv2;
        }
    }

    class ListItem {
        String mIndex1;
        String mValue1;
        String mChar1;
        String mIndex2;
        String mValue2;
        String mChar2;

        public ListItem(String index1, String value1, String char1,
                        String index2, String value2, String char2) {
            mIndex1 = index1;
            mValue1 = value1;
            mChar1 = char1;
            mIndex2 = index2;
            mValue2 = value2;
            mChar2 = char2;
        }
    }

}

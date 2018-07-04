package com.reveautomation.revesmartsecuritykit_online.example4_characteristic;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.utils.ConnectionSharingAdapter;
import com.reveautomation.revesmartsecuritykit_online.DeviceActivity;
import com.reveautomation.revesmartsecuritykit_online.R;
import com.reveautomation.revesmartsecuritykit_online.SampleApplication;
import com.reveautomation.revesmartsecuritykit_online.util.HexString;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

import static com.trello.rxlifecycle.android.ActivityEvent.PAUSE;

public class CharacteristicOperationExampleActivity extends RxAppCompatActivity {

    public static final String EXTRA_CHARACTERISTIC_UUID = "extra_uuid";
    public static final String EXTRA_CHARACTERISTIC_UUIDtest = "TEST";
    @BindView(R.id.connect)
    Button connectButton;
    @BindView(R.id.read_output)
    TextView readOutputView;
    @BindView(R.id.read_hex_output)
    TextView readHexOutputView;
    @BindView(R.id.write_input1)
    TextView writeInput1;


    @BindView(R.id.read)
    Button readButton;
    @BindView(R.id.freset)
    Button freset;
    @BindView(R.id.write)
    Button writeButton;
    @BindView(R.id.stopbtn)
    Button stopbtn;
    @BindView(R.id.notify)
    Button notifyButton;
    private UUID characteristicUuid;
    private UUID characteristicUuidniktest;
    private UUID characteristicUuidniktestnotify;
    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();
    private Observable<RxBleConnection> connectionObservable;
    private RxBleDevice bleDevice;
    int startflag = 0;
    int stopflag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example4);
        ButterKnife.bind(this);
        String macAddress = getIntent().getStringExtra(DeviceActivity.EXTRA_MAC_ADDRESS);
        characteristicUuid = (UUID) getIntent().getSerializableExtra(EXTRA_CHARACTERISTIC_UUID);
        characteristicUuidniktest = UUID.fromString("00001525-1212-efde-1523-785feabcd123");
        Log.d("characteriniktest",""+  characteristicUuidniktest);
        characteristicUuidniktestnotify = UUID.fromString("00001524-1212-efde-1523-785feabcd123");
        bleDevice = SampleApplication.getRxBleClient(this).getBleDevice(macAddress);
        connectionObservable = prepareConnectionObservable();
        //noinspection ConstantConditions
        getSupportActionBar().setSubtitle(getString(R.string.mac_address, macAddress));
    }
    private Observable<RxBleConnection> prepareConnectionObservable() {
        return bleDevice
                .establishConnection(false)
                .takeUntil(disconnectTriggerSubject)
                .compose(bindUntilEvent(PAUSE))
                .compose(new ConnectionSharingAdapter());
    }

    @OnClick(R.id.connect)
    public void onConnectToggleClick() {

        if (isConnected()) {
            triggerDisconnect();
        } else {
            connectionObservable
                    .flatMap(RxBleConnection::discoverServices)
                    .flatMap(rxBleDeviceServices -> rxBleDeviceServices.getCharacteristic(characteristicUuidniktest))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> connectButton.setText(R.string.connecting))
                    .subscribe(
                            characteristic -> {
                                updateUI(characteristic);
                                Log.i(getClass().getSimpleName(), "Hey, connection has been established!");
                            },
                            this::onConnectionFailure,
                            this::onConnectionFinished
                    );
        }
    }
    @OnClick(R.id.freset)
    public void onFresetClick() {

        writeInput1.setText("FF");

    }
        @OnClick(R.id.read)
    public void onReadClick() {

        if (isConnected()) {

            connectionObservable
                    .flatMap(rxBleConnection -> rxBleConnection.readCharacteristic(characteristicUuidniktest))//
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(bytes -> {
                        readOutputView.setText(new String(bytes));
                        readHexOutputView.setText(HexString.bytesToHex(bytes));
                        writeInput1.setText(HexString.bytesToHex(bytes));
                        Log.d("Read nikunj  ===",HexString.bytesToHex(bytes));
                    }, this::onReadFailure);
        }
    }
    @OnClick(R.id.stopbtn)
    public void onStopClick() {
        stopflag = 1;
        if (isConnected()) {
            connectionObservable
                    .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUuidniktest, getInputBytes()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bytes -> onWriteSuccess(),
                            this::onWriteFailure
                    );
        }

    }
    @OnClick(R.id.write)
    public void onWriteClick() {
        startflag = 1;
        if (isConnected()) {
            connectionObservable
                    .flatMap(rxBleConnection -> rxBleConnection.writeCharacteristic(characteristicUuidniktest, getInputBytes()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bytes -> onWriteSuccess(),
                            this::onWriteFailure
                    );
        }
    }

    @OnClick(R.id.notify)
    public void onNotifyClick() {

        if (isConnected()) {
            connectionObservable
                    .flatMap(rxBleConnection -> rxBleConnection.setupNotification(characteristicUuidniktestnotify))//defaul characteristicUuid change by nikunj
                    .doOnNext(notificationObservable -> runOnUiThread(this::notificationHasBeenSetUp))
                    .flatMap(notificationObservable -> notificationObservable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);
        }
    }

    private boolean isConnected() {
        return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;
    }

    private void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Connection error: " + throwable, Snackbar.LENGTH_SHORT).show();
        updateUI(null);
    }

    private void onConnectionFinished() {
        updateUI(null);
    }

    private void onReadFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Read error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void onWriteSuccess() {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Write success", Snackbar.LENGTH_SHORT).show();
    }

    private void onWriteFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Write error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void onNotificationReceived(byte[] bytes) {
        //noinspection ConstantConditions
        try {
            String val = new String (bytes , "UTF-8");
            Log.d("Converted string VAL",HexString.bytesToHex(bytes));
            Snackbar.make(findViewById(R.id.main), "Change: " +val, Snackbar.LENGTH_SHORT).show();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String st = String.valueOf(bytes);

        Log.d("Converted string",st);
        //
    }

    private void onNotificationSetupFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Notifications error: " + throwable, Snackbar.LENGTH_SHORT).show();
    }

    private void notificationHasBeenSetUp() {
        //noinspection ConstantConditions
        Snackbar.make(findViewById(R.id.main), "Notifications has been set up", Snackbar.LENGTH_SHORT).show();
    }

    private void triggerDisconnect() {
        disconnectTriggerSubject.onNext(null);
    }

    /**
     * This method updates the UI to a proper state.
     * @param characteristic a nullable {@link BluetoothGattCharacteristic}. If it is null then UI is assuming a disconnected state.
     */
    private void updateUI(BluetoothGattCharacteristic characteristic) {
        connectButton.setText(characteristic != null ? R.string.disconnect : R.string.connect);
      /*  readButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_READ));
        writeButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));
        stopbtn.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));
        notifyButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_NOTIFY));
        notifyButton.setEnabled(hasProperty(characteristic, BluetoothGattCharacteristic.PROPERTY_WRITE));// aad by nikunj
      */
        readButton.setEnabled(true);
        writeButton.setEnabled(true);
        stopbtn.setEnabled(true);
        notifyButton.setEnabled(true);
         // aad by nikunj

    }

    private boolean hasProperty(BluetoothGattCharacteristic characteristic, int property) {
        return characteristic != null && (characteristic.getProperties() & property) > 0;
    }
    public String single(String data){
        int st2 = Integer.parseInt(data.toString().trim());
        String wri15 = Integer.toHexString(st2);
        if (wri15.length() < 2){
            wri15 = String.format("%0"+ (2 - wri15.length() )+"d%s",0 ,wri15);
        }
        return wri15;
    }
    public String doubble(String data){
        int st = Integer.parseInt(data.toString().trim());
        String wri7 = Integer.toHexString(st);
        if (wri7.length() < 4) {
            wri7 = String.format("%0" + (4 - wri7.length()) + "d%s", 0, wri7);
        }
        return wri7;
    }
    private byte[] getInputBytes() {

        if (startflag == 1) {
            startflag = 0;
            Toast.makeText(this, "Start", Toast.LENGTH_LONG).show();
            return HexString.hexToBytes("");
        }else if (stopflag == 1){
            stopflag =0;
            Toast.makeText(this, "Stop", Toast.LENGTH_LONG).show();
            return HexString.hexToBytes("");
        } else {
            return HexString.hexToBytes("FF");
        }
    }
}

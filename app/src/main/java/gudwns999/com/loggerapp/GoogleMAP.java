package gudwns999.com.loggerapp;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;


public class GoogleMAP extends FragmentActivity implements
        GoogleMap.OnMapClickListener {
    int i=1;
    TextView text01;
    private ArrayList<LatLng> arrayPoints;
    private GoogleMap mGoogleMap;

    PolylineOptions pOptionsl = new PolylineOptions();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.google);

        // BitmapDescriptorFactory 생성하기 위한 소스
        MapsInitializer.initialize(getApplicationContext());

        init();
    }

    private void appendText(String msg) {
        text01.append(msg + "\n");
    }
    /** Map 클릭시 터치 이벤트 */
    public void onMapClick(LatLng point) {
        // 현재 위도와 경도에서 화면 포인트를 알려준다
        Point screenPt = mGoogleMap.getProjection().toScreenLocation(point);
        // 현재 화면에 찍힌 포인트로 부터 위도와 경도를 알려준다.
        LatLng latLng = mGoogleMap.getProjection().fromScreenLocation(screenPt);
        Log.d("맵좌표", "좌표: 위도(" + String.valueOf(point.latitude) + "), 경도("
                + String.valueOf(point.longitude) + ")");
        Log.d("화면좌표", "화면좌표: X(" + String.valueOf(screenPt.x) + "), Y("
                + String.valueOf(screenPt.y) + ")");
        Toast.makeText(this,"위도("+ String.valueOf(point.latitude) + "), 경도("
                + String.valueOf(point.longitude) + ")",Toast.LENGTH_LONG).show();
    }

    /**
     * 초기화
     * @author
     */
    private void init() {
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(GoogleMAP.this);
        mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        GpsInfo gps = new GpsInfo(GoogleMAP.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Showing the current location in Google Map
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Map 을 zoom 합니다.
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

            //시작 마커 설정. (우리집)
            MarkerOptions optFirst = new MarkerOptions();
            optFirst.position(new LatLng(37.6009768, 127.0152232));// 위도 • 경도
            optFirst.title("Start Position"); // 제목 미리보기
            optFirst.snippet("우리집");
            mGoogleMap.addMarker(optFirst).showInfoWindow();
            //끝 마커 설정. (국민대)
            MarkerOptions optSecond = new MarkerOptions();
            optSecond.position(new LatLng(37.6108733, 126.9951006));// 위도 • 경도
            optSecond.title("End Position"); // 제목 미리보기
            optSecond.snippet("국민대");
            mGoogleMap.addMarker(optSecond).showInfoWindow();

            PolylineOptions pOptions = new PolylineOptions();
            pOptions.color(Color.RED);
            pOptions.width(10);
            pOptions.add((new LatLng(37.6009768, 127.0152232)));
            pOptions.add((new LatLng(37.6108733, 126.9951006)));
            Polyline pLine = mGoogleMap.addPolyline(pOptions);
        }

    }

    public class GpsInfo extends Service implements android.location.LocationListener {

        private final Context mContext;

        // 현재 GPS 사용유무
        boolean isGPSEnabled = false;

        // 네트워크 사용유무
        boolean isNetworkEnabled = false;

        // GPS 상태값
        boolean isGetLocation = false;

        Location location;
        double lat; // 위도
        double lon; // 경도

        // GPS 정보 업데이트 거리 10미터
        private static final long MIN_DISTANCE_UPDATES = 1;

        // GPS 정보 업데이트 시간 1/1000
        private static final long MIN_TIME_UPDATES = 5000 * 1;

        protected LocationManager locationManager;

        public GpsInfo(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                } else {
                    this.isGetLocation = true;
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_UPDATES,
                                MIN_DISTANCE_UPDATES, this);

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                // 위도 경도 저장
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }

                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager
                                    .requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER,
                                            MIN_TIME_UPDATES,
                                            MIN_DISTANCE_UPDATES,
                                            this);
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location != null) {
                                    lat = location.getLatitude();
                                    lon = location.getLongitude();
                                }
                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return location;
        }

        /**
         * GPS 종료
         * */
        public void stopUsingGPS() {
            if (locationManager != null) {
                locationManager.removeUpdates(GpsInfo.this);
            }
        }

        /**
         * 위도값
         * */
        public double getLatitude() {
            if (location != null) {
                lat = location.getLatitude();
            }
            return lat;
        }

        /**
         * 경도값
         * */
        public double getLongitude() {
            if (location != null) {
                lon = location.getLongitude();
            }
            return lon;
        }

        public boolean isGetLocation() {
            return this.isGetLocation;
        }

        /**
         * GPS 정보를 가져오지 못했을때 설정값으로 갈지 물어보는 alert 창
         * */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    mContext);

            alertDialog.setTitle("GPS 사용유무셋팅");
            alertDialog
                    .setMessage("GPS 셋팅이 되지 않았을수도 있습니다.\n 설정창으로 가시겠습니까?");

            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(intent);
                        }
                    });
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

        public void onLocationChanged(Location location) {
            getLocation();
            double latitude = getLatitude();
            double longitude = getLongitude();
            LatLng[] latLng = new LatLng[100];

            // TODO Auto-generated method stub
            //현재 마커 설정.
            latLng[i] = new LatLng(latitude, longitude);

            MarkerOptions optNow = new MarkerOptions();
            optNow.position(latLng[i]);// 위도 • 경도
            optNow.title("Now Position");// 제목 미리보기
            optNow.snippet(""+i+"번째 위치");
            optNow.icon(BitmapDescriptorFactory.fromResource(R.drawable.gps));
            mGoogleMap.addMarker(optNow).showInfoWindow();

            Marker marker = mGoogleMap.addMarker(optNow);
            marker.setPosition(latLng[i]);


            pOptionsl.color(Color.BLUE);
            pOptionsl.width(5);
            pOptionsl.add(latLng[i]);
            Polyline pLine = mGoogleMap.addPolyline(pOptionsl);

            Log.d("맵좌표", "좌표: 위도(" + String.valueOf(latitude) + "), 경도("
                    + String.valueOf(longitude) + ")+("+i+" chance)");
            i++;
        }

        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            // TODO Auto-generated method stub

        }

        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
    }
}
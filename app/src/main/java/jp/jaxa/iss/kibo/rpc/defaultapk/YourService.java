package jp.jaxa.iss.kibo.rpc.defaultapk;


import android.graphics.Bitmap;
import android.util.Log;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.QRCodeDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    //Point qr_Point = new Point(11.21,-9.8,4.79);
    Point qr_Point = new Point(11.353-0.02,-10.096-0.05,4.9837+0.05);
    Quaternion qr_Quaternion = new Quaternion(0,0,-0.707f,0.707f);
    Point point_b = new Point(10.6,-8.0,4.5);
    Quaternion quaternion_b = new Quaternion(0,0,-0.707f,0.707f);
    String debugStr =  "MY DEBUG";
    String errorStr =  "MY ERROR";
    String ImagePath = "/sdcard/Pictures";
    @Override

    protected void runPlan1(){
        api.startMission();
       // api.getRobotKinematics();
       // api.getTrustedRobotKinematics();
        api.moveTo(qr_Point,qr_Quaternion,true);
       // api.moveTo(newMove,qr_Quaternion,true);
        //api.relativeMoveTo(qr_Point,qr_Quaternion,true);\
        Log.i(debugStr,"takeQr");
        takeQr();
        //api.moveTo(point_b,quaternion_b,true);
        //api.relativeMoveTo(point_b,quaternion_b,true);
        Log.i(debugStr,"takeSnapShot");
        takeSnapShot();
        Log.i(debugStr,"reportMissionCompletion");
        api.reportMissionCompletion();
    }

    @Override
    protected void runPlan2(){

        // write here your plan 2
    }

    @Override
    protected void runPlan3(){
        // write here your plan 3
    }

    void takeQr(){
        //this.gsService.sendData(MessageType.JSON, "data", data2.toString());
        api.flashlightControlFront(1.0F);
        Mat image = api.getMatNavCam();
        api.flashlightControlFront(0.0F);
        image = cropMatimage(image);
       // saveImage(image);
        QRCodeDetector qrcodeDetector = new QRCodeDetector();
        String content  = qrcodeDetector.detectAndDecode(image);
        //Log.i("qrcodeDetector",content);
        if(!content.isEmpty()) {
            api.sendDiscoveredQR(content);
            if(content.equals("1")){
                //saveImage(image);
            }
            else{

            }
        }
        else {
            //if in local simulation
            Log.e("qrcodeDetector","NOT FOUND");
            api.reportMissionCompletion();

        }
        //Log.i("Mat getMatNavCam",image.toString());

    }
    void takeSnapShot(){
        //api.laserControl(true);
        api.takeSnapshot();
        //api.laserControl(false);
    }
    Mat cropMatimage(Mat image){
       // Rect size1 = new Rect(439,815,307,402);
       // Rect size1 = new Rect(new Point(434,411,0),new Point(746,815,0));
        //Log.i("image size",image.size().toString());
        Rect size1 = new Rect(444,411,337,402);
        return image.submat(size1);
        //return  new Mat(image,size1);
    }
    void saveImage(Mat image){
        //src = Imgcodecs.("test.jpg")
       // Mat image = api.getMatNavCam();
        Log.i(debugStr,"saveImage");
       // File mydir = this.getFilesDir();
        //File dataDir = this.getDataDir();
        try {

           // dataDir.toPath();
         //   Log.i("mydir",mydir.toString());
          //  Log.i("dataDir",dataDir.toString());
            Imgcodecs.imwrite(ImagePath+"/Mat.png", image);
            //Log.i("dataDir",dataDir.toString());
        }
        catch (CvException e){
            Log.e(errorStr,e.toString());
        }
        try {
            Log.i(debugStr,"BitMap");
            Bitmap bitimage = api.getBitmapNavCam();
            Log.i("BitMap getBitmapNavCam",bitimage.toString());
            FileOutputStream out = new FileOutputStream(ImagePath+"/Bitmap.png");
            bitimage.compress(Bitmap.CompressFormat.PNG,100,out);
            out.flush();
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


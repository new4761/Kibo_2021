package jp.jaxa.iss.kibo.rpc.defaultapk;


import android.util.Log;

import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.android.gs.MessageType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import org.opencv.core.Mat;
import org.opencv.objdetect.QRCodeDetector;

/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    Point qr_Point = new Point(11.21,-9.8,4.79);
    Quaternion qr_Quaternion = new Quaternion(0,0,-0.707f,0.707f);
    Point point_b = new Point(10.6,-8.0,4.5);
    Quaternion quaternion_b = new Quaternion(0,0,-0.707f,0.707f);
    @Override

    protected void runPlan1(){
        api.startMission();
       // api.getRobotKinematics();
       // api.getTrustedRobotKinematics();
        api.moveTo(qr_Point,qr_Quaternion,true);

        //api.relativeMoveTo(qr_Point,qr_Quaternion,true);
        takeQr();
        api.moveTo(point_b,quaternion_b,true);
        //api.relativeMoveTo(point_b,quaternion_b,true);
        takeSnapShot();
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
        QRCodeDetector qrcodeDetector = new QRCodeDetector();
        String content  = qrcodeDetector.detectAndDecode(image);
        Log.i("qrcodeDetector",content);
        Log.i("Mat getMatNavCam",image.toString());

    }
    void takeSnapShot(){
        api.laserControl(true);
        api.takeSnapshot();
        api.laserControl(false);
    }


}


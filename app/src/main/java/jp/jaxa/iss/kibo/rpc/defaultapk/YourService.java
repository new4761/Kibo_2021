package jp.jaxa.iss.kibo.rpc.defaultapk;


import android.graphics.Bitmap;
import android.util.Log;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcApi;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;


import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.QRCodeDetector;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee
 */

public class YourService extends KiboRpcService {

    Point qr_Point = new Point(11.351,-10.094,4.87);
  //  Point qr_Point = new Point(11.353-0.02,-10.096-0.05,4.9837+0.05);
 //   Point qr_Point = new Point(11.353-0.02,-10.096-0.05,4.9837+0.05);
    Quaternion qr_Quaternion = new Quaternion(0,0,-0.707f,0.707f);
    Point point_b = new Point(10.6,-8.0,4.5);
    Quaternion quaternion_b = new Quaternion(0,0,-0.707f,0.707f);
    String debugStr =  "MY DEBUG";
    String errorStr =  "MY ERROR";
    String ImagePath = "/sdcard/Pictures";
    @Override

    protected void runPlan1(){
        api.startMission();

        api.moveTo(qr_Point,qr_Quaternion,true);
        Log.i(debugStr,"takeQr");
        takeQr();

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
        //api.flashlightControlFront(1.0F);
        Mat image = api.getMatNavCam();
        //api.flashlightControlFront(0.0F);
       // image = cropMatimage(image);

       /* image = contours(image);
        saveImage(image);
        QRCodeDetector qrcodeDetector = new QRCodeDetector();
        String content  = qrcodeDetector.detectAndDecode(image); */
        String content = contours(image,true);
        Log.i("qrcodeDetector",content);
        if(!content.isEmpty()) {
            api.sendDiscoveredQR(content);
            if(content.equals("1")){
                //saveImage(image);
                takeSnapShot();
                api.reportMissionCompletion();
            }
            else{
                takeSnapShot();
                api.reportMissionCompletion();
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
    String contours(Mat image,Boolean saveImage) {
        QRCodeDetector qrcodeDetector = new QRCodeDetector();
        Mat binary = new Mat(image.rows(),image.cols(),image.type(),new Scalar(0));
        Imgproc.threshold(image,binary,0,255,Imgproc.THRESH_BINARY_INV);
        Mat kernel= Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(150,150));
        Mat mask = new Mat();;
        Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_CLOSE,kernel);
        //Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_OPEN,kernel);
        //Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_DILATE,kernel);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hirearchey = new Mat();
        Mat con_img = image.clone();
        Imgproc.findContours(mask,contours,hirearchey,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
        Size minsize =new Size(100,100);
        String content  ="";
        for(int i = 0; i < contours.size(); i++) {
           // double contourArea = Imgproc.contourArea(contours.get(i));


            Rect rec =Imgproc.boundingRect(contours.get(i));
            if(rec.area()< minsize.area()||rec.width<rec.height)
                continue;
            Log.i("rec.area()  "+i,Double.toString(rec.area()));
            //rec.
            Log.i("contours x "+i,Integer.toString(rec.x)+":"+Integer.toString(rec.y));
            Log.i("contours size "+i,Integer.toString(rec.width)+":"+Integer.toString(rec.height));
            /*if(rec.width<rec.height)
                continue; */
            //rec.width = rec.width+25;
            //rec.height = rec.height+25;
            Mat cropContours = image.submat(rec);
            //Core.bitwise_not(cropContours,cropContours);
            if(saveImage) {
                Imgcodecs.imwrite(ImagePath + "/contours_" + i + ".png", cropContours);
                Imgproc.drawContours(con_img,contours,i,new Scalar(0,0,255),2);
            }

            content  = qrcodeDetector.detectAndDecode(cropContours);
            //String content  = qrcodeDetector.decode(cropContours);
            Log.i("content "+i,content);
            if(!content.isEmpty()) {
                Log.i("content ",content);
                break;
            }
        }
        if(saveImage) {
            Imgcodecs.imwrite(ImagePath + "/mask.png", mask);
            Imgcodecs.imwrite(ImagePath + "/binary.png", binary);
            Imgcodecs.imwrite(ImagePath + "/Mat.png", image);
            Imgcodecs.imwrite(ImagePath + "/Con.png", con_img);
        }
        return content;
    }
    Mat contours(Mat image){
        //Mat src = image.clone();
        //Imgproc.GaussianBlur(image,image,new Size(5,5),5);
        Imgcodecs.imwrite(ImagePath+"/blur.png", image);
        Mat binary = new Mat(image.rows(),image.cols(),image.type(),new Scalar(0));
        Imgproc.threshold(image,binary,0,255,Imgproc.THRESH_BINARY_INV);

        Imgcodecs.imwrite(ImagePath+"/binary.png", binary);

        Mat kernel= Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(100,100));
        Mat mask = new Mat();
       // Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_DILATE,kernel);
        Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_CLOSE,kernel);
        Imgcodecs.imwrite(ImagePath+"/mask.png", mask);


        List<MatOfPoint> contours = new ArrayList<>();
        Mat hirearchey = new Mat();
        Imgproc.findContours(mask,contours,hirearchey,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);

        /*  List<MatOfPoint> contours = new ArrayList<>();
        Mat hirearchey = new Mat();
        Imgproc.findContours(binary,contours,hirearchey,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE); */

        //find qrcode contour
       // MatOfPoint cou

        MatOfPoint2f[] contoursPoly = new MatOfPoint2f[contours.size()];
        Rect[] boundRect = new Rect[contours.size()];


        for(int i = 0; i < contours.size(); i++) {
            //if(contour)
            contoursPoly[i] = new MatOfPoint2f();
            Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly[i], 0.1,  true);
            boundRect[i] = Imgproc.boundingRect(new MatOfPoint(contours.get(i).toArray()));
            Rect rec =Imgproc.boundingRect(contours.get(i));
            Mat cropContours = image.submat(rec);
            Imgcodecs.imwrite(ImagePath+"/contours_"+i+".png", cropContours);
        }



/*        for(int i=0;i<contours.size();i++){

            Imgcodecs.imwrite(ImagePath+"/contours_"+i+".png", contours.get(i));
            Rect rec =Imgproc.boundingRect(contours.get(i));
            Mat cropContours = image.submat(rec);
        }  */
        Rect rec =Imgproc.boundingRect(contours.get(0));
        Mat cropContours = image.submat(rec);
        return cropContours;
    }


}


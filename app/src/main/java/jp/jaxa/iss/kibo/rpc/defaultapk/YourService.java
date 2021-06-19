package jp.jaxa.iss.kibo.rpc.defaultapk;


import android.graphics.Bitmap;
import android.util.Log;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
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
        //saveImage(image);
        QRCodeDetector qrcodeDetector = new QRCodeDetector();
        String content  = qrcodeDetector.detectAndDecode(image);
        //Log.i("qrcodeDetector",content);
        if(!content.isEmpty()) {
            api.sendDiscoveredQR(content);
            if(content.equals("1")){
                saveImage(image);
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
        Rect size1 = new Rect(283,493,632,461);
        image = image.submat(size1);
        //Size sz = image.size();
        //HoughLine(image);
        //persPectivetransform(image);
        image = contours(image);
        return image;
    }
    Mat contours(Mat image){
        Mat binary = new Mat(image.rows(),image.cols(),image.type(),new Scalar(0));
        Imgproc.threshold(image,binary,0,255,Imgproc.THRESH_BINARY_INV);
        Mat kernel= Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(100,100));
        Mat mask = new Mat();
        Imgproc.morphologyEx(binary,mask,Imgproc.MORPH_DILATE,kernel);
        //Imgcodecs.imwrite(ImagePath+"/mask.png", mask);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hirearchey = new Mat();
        Imgproc.findContours(mask,contours,hirearchey,Imgproc.RETR_TREE,Imgproc.CHAIN_APPROX_SIMPLE);
       // Scalar color = new Scalar(0,0,255);
        //Imgcodecs.imwrite(ImagePath+"/binary.png", binary);
       // Mat output = image.clone();
        //for(MatOfPoint p :contours){
            //Log.i("MatOfPoint :"+p+" ",p.toString());
            Rect rec =Imgproc.boundingRect(contours.get(0));
            Mat cropContours = new Mat(image,rec);
            Imgcodecs.imwrite(ImagePath+"/cropContours.png", cropContours);
       // }

       // Imgproc.drawContours(output,contours,-1,color,2,Imgproc.LINE_8,hirearchey,2,new org.opencv.core.Point());
       // Imgcodecs.imwrite(ImagePath+"/contours.png", output);
        //Log.i("binary",binary.dump());
        //Imgcodecs.imwrite(ImagePath+"/binary.png", binary);
       // Core.bitwise_not(binary,binary);
        return cropContours;
    }
    void persPectivetransform(Mat image){
        Mat dst_Image = image.clone();
        org.opencv.core.Point point1= new org.opencv.core.Point();
        org.opencv.core.Point point2= new org.opencv.core.Point();
        org.opencv.core.Point point3= new org.opencv.core.Point();
        org.opencv.core.Point point4= new org.opencv.core.Point();
        Mat src = new MatOfPoint2f(point1,point2,point3,point4);
        Mat dst = new MatOfPoint2f(new  org.opencv.core.Point(0,0),new  org.opencv.core.Point(dst_Image.width()-1,0)
                ,new  org.opencv.core.Point(dst_Image.width()-1,dst_Image.height()-1),new  org.opencv.core.Point(0,dst_Image.height()-1));
        Mat transform = Imgproc.getPerspectiveTransform(src,dst);
        Imgproc.warpPerspective(image,dst_Image,transform,dst_Image.size());
        Imgcodecs.imwrite(ImagePath+"/persPectivetransform.png", dst_Image);

    }

    void HoughLine(Mat image){
        //Mat gray =new Mat();
        //Imgproc.cvtColor(image,gray,Imgproc.COLOR_RGBA2GRAY);

        Mat output = image.clone();

        Mat lines = new Mat();
        Mat edges = new Mat();
       //
        Imgproc.Canny(output,edges,10,50);
        //Imgproc.HoughLines(edges,lines,1 ,Math.PI/180,50,50,10);
        Imgproc.HoughLinesP(edges,lines,1 ,Math.PI/180,50,50,10);
        Imgcodecs.imwrite(ImagePath+"/edges.png", edges);

        for(int i = 0; i < lines.cols(); i++) {
            double[] val = lines.get(0, i);
            Imgproc.line(output, new  org.opencv.core.Point(val[0], val[1]), new  org.opencv.core.Point(val[2], val[3]), new Scalar(0, 0, 255), 2);
        }

        //Imgcodecs.imwrite(ImagePath+"/lines.png", lines);
        Imgcodecs.imwrite(ImagePath+"/HoughLine.png", output);
    }
    void saveImage(Mat image){
        Log.i(debugStr,"saveImage");
        try {
            Imgcodecs.imwrite(ImagePath+"/Mat.png", image);
           ;
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


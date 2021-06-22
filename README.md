<div style="text-align:center" >
<h1>This project make for participate in <a href="https://www.nstda.or.th/jaxa-thailand/2nd-kibo-rpc/?fbclid=IwAR2JVeayPQReryjPBe6Mb-aSk8ZPst0TJGOcX8gOmBRFpM4Qe5MwdZE7O9Q">The 2nd Kibo Robot Programming Challenge </a>
</h1> 
</div>

> 6 - 21 june 2020

## About This event
<p>
Kibo Robot Programming Challenge is organizes by  <b>Japan Aerospace Exploration Agency: JAXA</b> and <b>National Aeronautics and Space Administration: NASA in </b> in Thailand we organize by 
<b>National Science and Technology Development Agency (NSTDA)</b> 
</p>
<p>
Any team who participate need to develop Android apk (JAVA) to control <a href="https://github.com/nasa/astrobee">Astrobee robot </a> through  <a href="https://github.com/nasa/astrobee_android"> Guest Science for android</a>
for do the objective.</p>

> Astrobee is simulation running on ros program

### Objective for 2020

<ol>
    <li>Move Astrobee to qrcode postion and read string from qrcode</li>
    <li>use string data to move astrobee to next position and point laser</li>
</ol>

[Link for rulebook](https://jaxa.krpc.jp/download.html)
> when astrobee is need to be in Keep in zone and don't touch Keep out zone 

### How is hard (my experience)
<p>
when you need to test your astrobee is need to be running on simulation and my simulation and website simulation is not same version and not same qr so tough to debug image and debug value on website simulation
</p>

### What we do here

-  We used opencv to work with image processing 
    
<p>Our project is using contour algorithm to trying for detach QR CODE and is work well for local simulation but too bad on web simulation that make we stuck at objective 1 that mean we can't make is through the 2nd round </p>

> Is need to used too much time for debug and fixing code while we have time left 1 day so that the end 



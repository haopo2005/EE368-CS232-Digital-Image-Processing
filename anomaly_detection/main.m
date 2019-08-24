clear;
clc;
close all;

img1 = imread('20161121134841110.png');
img0 = imread('20161121134544657.png');

% Alignment of background frame and incoming frame
% not necessary in this particular case since camera stayed stable
% comment out to speed up the calculation

points0 = detectSURFFeatures(rgb2gray(img0));
points1 = detectSURFFeatures(rgb2gray(img1));
figure;
subplot(1,2,1), imshow(img0); hold on;
plot(points0.selectStrongest(20));
subplot(1,2,2), imshow(img1); hold on;
plot(points1.selectStrongest(20));

[f0,vpts0] = extractFeatures(rgb2gray(img0),points0);
[f1,vpts1] = extractFeatures(rgb2gray(img1),points1);

indexPairs = matchFeatures(f0,f1);
matchedPoints0 = vpts0(indexPairs(:,1));
matchedPoints1 = vpts1(indexPairs(:,2));

figure; 
showMatchedFeatures(img0,img1,matchedPoints0,matchedPoints1);


% generating a time sequence of incoming frames
tic
% region for background - foreground intensity normalization
ROI = [50,200,1,140];
% define the melt line
lhbound = [380,495];
% define the size for erosion and dilation, characteristic size of the
% black reflection in the molten zone
disksize = 25;
% readin time lapse image roll
imageNames = dir(fullfile('Floating Zone','testRoll2','*.png'));
imageNames = {imageNames.name}';
% start writing the output video
testroll2 = VideoWriter(fullfile('Floating Zone','testroll2.avi'));
testroll2.FrameRate = 15;
open(testroll2);

% initialize parameter arrays
intensity = zeros(length(imageNames),1);
intensity_fgd = zeros(length(imageNames),1);
ledge = zeros(lhbound(2)-lhbound(1)+1,2,length(imageNames));
redge = ledge;
edge_slope = ledge;
totalN = size(1:3:length(imageNames)-2,2);
CoM = zeros(length(imageNames),2);
CoManomaly = zeros(length(imageNames),1);
Vol = zeros(length(imageNames),1);
VolAnomaly = zeros(length(imageNames),1);
VolAlertXY = zeros(length(imageNames),2);
CoMAlertXY = zeros(length(imageNames),2);
IntAnomaly = zeros(length(imageNames),1);
IntAlertXY = zeros(length(imageNames),2);
% define the time window for anomaly estimator
timewindow = 25;
% define the history window for CoM display
histnum = 50;

% frame-by-frame processing
for ii = 1:3:length(imageNames)-2
   nn = (ii+2)/3;
   % read every three frames to reduce aliasing
   img1 = imread(fullfile('Floating Zone','testRoll2',imageNames{ii}));
%   img2 = imread(fullfile('Floating Zone','testRoll2',imageNames{ii+1}));
%   img3 = imread(fullfile('Floating Zone','testRoll2',imageNames{ii+2}));
%   img = uint8((double(img1) + double(img2) + double(img3))/3);
   img1 = norm2bkg(img0,img1,ROI);
   % calculate the reflectivity in the middle of the zone
   imgcrop = img1(414:458,212:364,:);
   intensity(ii) = mean(imgcrop(:));
   % extract the zone mask and edges
   [imgmask, ledge(:,:,ii), redge(:,:,ii)] = extract_mask(img0,img1,lhbound,disksize);
   imgmask = double((1-imgmask))*5 + double(imgmask);
   img1_masked = uint8(double(img1).*repmat(imgmask,[1,1,3]));
   
   imgdiff = abs(double(img1) - double(img0)).*10;
   imgcrop = imgdiff(414:458,212:364,:);
   intensity_fgd(ii) = mean(imgcrop(:));
   
   % extract the volume information and edge slope information
   vinfo = extract_volumeinfo(ledge(:,:,ii), redge(:,:,ii));
   CoM(ii,:) = vinfo{2};
   tempCoMx = nonzeros(CoM(:,1));
   tempCoMy = nonzeros(CoM(:,2));
   Vol(ii) = vinfo{1};
   tempVol = nonzeros(Vol);
   tempInt = nonzeros(intensity_fgd);
   edge_slope(:,:,ii) = vinfo{5};
   
   % bayesian estimator to estimate anomaly as soon as time elapsed to more
   % than 3 data points
   if(nn > 3)
       meanCoMxy = [mean(tempCoMx(max([1,nn-timewindow]):nn-1)), mean(tempCoMy(max([1,nn-timewindow]):nn-1))];
       sigmaCoMxy = [std(tempCoMx(max([1,nn-timewindow]):nn-1)), std(tempCoMy(max([1,nn-timewindow]):nn-1))].*2;
	   %t-distribution estimated
       CoManomaly(ii) = 1/2/pi/sigmaCoMxy(1)/sigmaCoMxy(2)*exp(-(CoM(ii,1)-meanCoMxy(1))^2/2/sigmaCoMxy(1)^2)*exp(-(CoM(ii,2)-meanCoMxy(2))^2/2/sigmaCoMxy(2)^2);
       
	   meanVol = mean(tempVol(max([1,nn-timewindow]):nn-1));
       sigmaVol = std(tempVol(max([1,nn-timewindow]):nn-1)).*2;
	   %t-distribution estimated
       VolAnomaly(ii) = 1/sqrt(2*pi)/sigmaVol*exp(-(Vol(ii)-meanVol)^2/2/sigmaVol^2);
       
	   meanInt = mean(tempInt(max([1,nn-timewindow]):nn-1));
       sigmaInt = std(tempInt(max([1,nn-timewindow]):nn-1)).*2;
	   %t-distribution estimated
       IntAnomaly(ii) = 1/sqrt(2*pi)/sigmaInt*exp(-(intensity_fgd(ii)-meanInt)^2/2/sigmaInt^2);
   end
   
   imshow(img1_masked);
   rectangle('Position',[2,2,295,200],'FaceColor','w');
   rectangle('Position',[300,2,295,200],'FaceColor','w');
   rectangle('Position',[2,600,295,200],'FaceColor','w');
   rectangle('Position',[300,600,295,200],'FaceColor','w');
   
   % generate yellow flicking background whenever a channel shows positive
   % anomaly detection
   if(-log(VolAnomaly(ii)+1e-99) > 8.5)
       VolAlertXY(ii,:) = [ii,Vol(ii)];         
       rectangle('Position',[300,2,295,200],'FaceColor','yellow');
       annotation('textbox',[.79 .93 .1 0.02],'String','ALERT!','Color','red','FontWeight','bold','LineStyle','none');
   end
   if(-log(CoManomaly(ii)+1e-99) > 5.7)
       CoMAlertXY(ii,:) = CoM(ii,:);    
       rectangle('Position',[2,600,295,200],'FaceColor','yellow');  
       annotation('textbox',[.40 .26 .1 0.02],'String','ALERT!','Color','red','FontWeight','bold','LineStyle','none'); 
   end
   if(-log(IntAnomaly(ii)+1e-99) > 6.2)
       IntAlertXY(ii,:) = [ii,intensity_fgd(ii)]; 
       rectangle('Position',[2,2,295,200],'FaceColor','yellow'); 
       annotation('textbox',[.40 .93 .1 0.02],'String','ALERT!','Color','red','FontWeight','bold','LineStyle','none');     
   end
   
   hold on;
   % plot reflectivity
   plot(200-0.25*intensity_fgd(1:3:ii),'XData',1:1/(totalN-1)*299:(nn-1)/(totalN-1)*299+1);
   plot(200-0.25*intensity_fgd(ii),'-o','MarkerFaceColor','r','XData',(nn-1)/(totalN-1)*299+1);   
   plot(200-0.25*IntAlertXY(1:3:ii,2),'.','MarkerEdgeColor','r','XData',(IntAlertXY(1:3:ii,1)/3-1/3)/(totalN-1)*299+1);
   
   % plot zone volume
   plot(200-Vol(1:3:ii)/125,'XData',301:1/(totalN-1)*299:(nn-1)/(totalN-1)*299+301);
   plot(200-Vol(ii)/125,'-o','MarkerFaceColor','r','XData',(nn-1)/(totalN-1)*299+301);
   plot(200-VolAlertXY(1:3:ii,2)/125,'.','MarkerEdgeColor','r','XData',(VolAlertXY(1:3:ii,1)/3-1/3)/(totalN-1)*299+301);
   
   % plot center of mass and its trajectory
   plot(CoM(ii,2),CoM(ii,1),'h','MarkerFaceColor','g');
   plot(15/4*CoM(max([1,ii-3*histnum]):3:ii,2)-890,-40/3*CoM(max([1,ii-3*histnum]):3:ii,1)+19600/3);
   plot(15/4*CoM(ii,2)-890,-40/3*CoM(ii,1)+19600/3,'-o','MarkerFaceColor','r');
   plot(15/4*CoMAlertXY(max([1,ii-3*histnum]):3:ii,2)-890,-40/3*CoMAlertXY(max([1,ii-3*histnum]):3:ii,1)+19600/3,'+','MarkerEdgeColor','r');
   
   % plot zone radius vs height
   plot(vinfo{4}*2+100,45+1.5*lhbound(1):1.5:45+1.5*lhbound(2),'LineWidth',1.5);
   
   % plot zone curvature vs height
   plot(smooth(vinfo{5}(:,1)*50+500,7),45+1.5*lhbound(1):1.5:45+1.5*lhbound(2),'LineWidth',1.5);
   plot(smooth(vinfo{5}(:,2)*50+500,7),45+1.5*lhbound(1):1.5:45+1.5*lhbound(2),'LineWidth',1.5);
   hold off
   
   annotation('textbox',[.13 .71 .1 0.02],'String',[num2str((ii-1)/3*0.4,'% 4.1f'),' hrs  (FF x 1800 times)'],'FitBoxToText','on');
   annotation('textbox',[.13 .93 .1 0.02],'String','reflectivity','FitBoxToText','on');
   annotation('textbox',[.52 .93 .1 0.02],'String','volume','FitBoxToText','on');
   annotation('textbox',[.13 .26 .1 0.02],'String','center of mass','FitBoxToText','on');
   annotation('textbox',[.52 .26 .1 0.02],'String','diameter','FitBoxToText','on');
   annotation('textbox',[.77 .26 .1 0.02],'String','gradient','FitBoxToText','on');
   
   % write current frame to video
   F = getframe(gcf);
   [imgX, ~] = frame2im(F);
   %writeVideo(testroll2,imgX);
   %close all
end
close(testroll2);

toc

% compute the binary positive detection result time sequence
avol = (-log(VolAnomaly(1:3:2440)+1e-99) > 8.5);
acom = (-log(CoManomaly(1:3:2440)+1e-99) > 5.7);
aint = (-log(IntAnomaly(1:3:2440)+1e-99) > 6.2);

% manual label of 'true' abnormal region
areal = aint.*0;
areal([1:102,187:210,301:362,435:480,566:600,626:660,728:770]) = 1;
areal = logical(areal);

% compute positive detection rate
prate_int = sum(smooth(aint,timewindow/2) & smooth(areal,timewindow/2))/sum(logical(smooth(areal,timewindow/2)));
prate_vol = sum(smooth(avol,timewindow/2) & smooth(areal,timewindow/2))/sum(logical(smooth(areal,timewindow/2)));
prate_com = sum(smooth(acom,timewindow/2) & smooth(areal,timewindow/2))/sum(logical(smooth(areal,timewindow/2)));
prate_tot = sum((smooth(aint,timewindow/2) + smooth(avol,timewindow/2) + smooth(acom,timewindow/2)) & smooth(areal,timewindow/2))/sum(logical(smooth(areal,timewindow/2)));

% plot reflectivity and the identified anomalies
figure,
plot(intensity_fgd(1:3:2440));
hold on,
plot(IntAlertXY(1:3:2440,2),'.','MarkerEdgeColor','r');
hold off

% plot the zone volume and the identified anomalies
figure,
plot(Vol(1:3:2440));
hold on,
plot(VolAlertXY(1:3:2440,2),'.','MarkerEdgeColor','r');
hold off

% plot anomaly probability across three detection channels
figure,
plot(-log(IntAnomaly(1:3:2440)+1e-99));
hold on,
plot(-log(VolAnomaly(1:3:2440)+1e-99));
plot(-log(CoManomaly(1:3:2440)+1e-99));
hold off

% plot anomaly detection results across three detection channels
figure,
scatter(1:814,aint*0.9);
hold on,
scatter(1:814,avol*0.8);
scatter(1:814,acom*0.7);
scatter(1:814,areal*1);
hold off

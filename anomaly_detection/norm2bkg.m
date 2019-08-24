function imgnorm = norm2bkg(imgbkg, img, ROI)

    assert(size(imgbkg,1) == size(img,1) && size(imgbkg,2) == size(img,2),'image sizes should be identical');

    if nargin < 3
        ROI = [1,size(img,1),1,size(img,2)];
    end % optional parameter
    
    % selects region of interest
    imgbkgROI = imgbkg(ROI(1):ROI(2),ROI(3):ROI(4),:);
    imgROI = img(ROI(1):ROI(2),ROI(3):ROI(4),:);
    
    % mean of the ROI
    meanintbkg = sum(imgbkgROI(:));
    meanint = sum(imgROI(:));
    
    % normalize intensity within ROI to compensate for exposure time 
    imgnorm = img.*(meanintbkg/meanint);
end
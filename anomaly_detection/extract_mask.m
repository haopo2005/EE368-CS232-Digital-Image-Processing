function [imgmask, ledge, redge] = extract_mask(imgbkg,imgnorm,lhbound,disksize)

    assert(size(imgbkg,1) == size(imgnorm,1) && size(imgbkg,2) == size(imgnorm,2),'image sizes should be identical');
    assert(disksize > 2,'disksize must be greater than 2');
    if nargin < 4
        disksize = 15;
        if nargin < 3
            lhbound = [2, size(imgnorm,1)-1];
        end % optional parameter
    end
    
    % extract molten zone mask
    imgbw = imbinarize(imdilate(rgb2gray(uint8(abs(double(imgbkg)-double(imgnorm)))),strel('disk',disksize)));
    imgbwfill = (bwlabel(1-imgbw)==1);
    imgmask = imerode(1-imgbwfill,strel('disk',disksize-2));
    %assign zero to the area out of lhbound(upper,bottom)
    imgmask(cat(2,1:lhbound(1)-1,lhbound(2)+1:size(imgnorm,1)),:) = 0;
    
    % find the left and right edges
    ledge = zeros(lhbound(2)-lhbound(1)+1,2);
    redge = ledge;
    for ii = lhbound(1):lhbound(2)
        for jj = 1:size(imgnorm,2)%iterate the cols of imgnorm, from left to right
            if(imgmask(ii,jj))
                ledge(ii-lhbound(1)+1,:) = [ii,jj];
                break;
            end
        end % find left edge
        for jj = size(imgnorm,2):-1:1%iterate the cols of imgnorm, from right to left
            if(imgmask(ii,jj))
                redge(ii-lhbound(1)+1,:) = [ii,jj];
                break;
            end
        end % find right edge
        
        if(~ledge(ii-lhbound(1)+1,2) || ~redge(ii-lhbound(1)+1,2))
            continue;
        end
        % fill in the mask between l and r edges
        for jj =  ledge(ii-lhbound(1)+1,2): redge(ii-lhbound(1)+1,2)
            imgmask(ii,jj) = 1;
        end
    end
    
end
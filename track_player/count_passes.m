function [num_passes,player_regions] = count_passes(disc_cents,p1_cents,...
    p2_cents,startFrame,endFrame,width,height)
%counts the number of passes which occur in the clip
num_passes = 0;
player_regions = zeros(720,1280,endFrame);
for i = startFrame:endFrame
    cent_x = round(p1_cents(i,1));
    cent_y = round(p1_cents(i,2));
    lower_y = cent_y-(height/3);
    upper_y = cent_y+(2*(height/3));
    lower_x = cent_x-(width/2);
    upper_x = cent_x+(width/2);
    if lower_y <= 0
        lower_y = 1;
    end
    if upper_y > 720
        upper_y = 720;
    end
    if lower_x <= 0
        lower_x = 1;
    end
    if upper_x > 1280
        upper_x = 1280;
    end
    player_regions(lower_y:upper_y,lower_x:upper_x,i) = 1;
    
    cent_x = round(p2_cents(i,1));
    cent_y = round(p2_cents(i,2));
    lower_y = cent_y-(height/3);
    upper_y = cent_y+(2*(height/3));
    lower_x = cent_x-(width/2);
    upper_x = cent_x+(width/2);
    if lower_y <= 0
        lower_y = 1;
    end
    if upper_y > 720
        upper_y = 720;
    end
    if lower_x <= 0
        lower_x = 1;
    end
    if upper_x > 1280
        upper_x = 1280;
    end
    player_regions(lower_y:upper_y,lower_x:upper_x,i) = 1;
end
   
%count via centroid location
count = 0;
in_region_prev = false;
    for i = startFrame:endFrame
        if disc_cents(i,1) < p1_cents(i,1) + (width/2) && ...
                disc_cents(i,1) > p1_cents(i,1) - (width/2) && ...
                disc_cents(i,2) < p1_cents(i,2) + (height/3) && ...
                disc_cents(i,2) > p1_cents(i,2) - 2*(height/3)
            in_region = true;
        elseif disc_cents(i,1) < p2_cents(i,1) + (width/2) && ...
                disc_cents(i,1) > p2_cents(i,1) - (width/2) && ...
                disc_cents(i,2) < p2_cents(i,2) + (height/3) && ...
                disc_cents(i,2) > p2_cents(i,2) - 2*(height/3)
            in_region = true;
        elseif disc_cents(i,:) == [0,0]
            in_region = true;
        else
            in_region = false;
        end
        if in_region_prev == true && in_region == false
            count = count + 1;
        end
        in_region_prev = in_region;
    end
    num_passes = count;
end


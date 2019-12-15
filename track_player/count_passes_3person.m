    
function [num_passes,player_regions] = count_passes(disc_cents,p1_cents,p2_cents,p3_cents,numFrames,width,height)
%counts the number of passes which occur in the clip
num_passes = 0;
player_regions = zeros(720,1280,numFrames);
for i = 1:numFrames
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
    cent_x = round(p3_cents(i,1));
    cent_y = round(p3_cents(i,2));
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
    %idea: every time the number of regions in the image is 3, increment
    %counter
end

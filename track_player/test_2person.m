clear, close all
vidobj = VideoReader('test_2person.mp4');
numFrames = get(vidobj,'NumberOfFrames');
%%
SE = strel('rectangle',[2 30]);
SE2 = strel('line',30,15);
SE3 = strel('line',30,175);
centroids_disc = zeros(numFrames,2);
disc_locations = zeros(720,1280,numFrames);
player_locations = zeros(size(disc_locations));
final_im = zeros(720,1280);
final_im = logical(final_im);
r_lim = [150 255];
g_lim = [0 150];
b_lim = [0 150];

%%
centroids_p1 = zeros(numFrames,2);
centroids_p2 = zeros(size(centroids_p1));
for i = 1:numFrames
    %locate players using locate_players function
    frame_col = read(vidobj,i);
    player_locations(:,:,i) = locate_players(frame_col,r_lim,g_lim,b_lim,500);
    Ilabel = bwlabel(player_locations(:,:,i));
    stat = regionprops(Ilabel,'centroid');
    if size(stat) == [2 1]
        cents = cat(1,stat.Centroid);
        centroids_p1(i,:) = cents(1,:);
        centroids_p2(i,:) = cents(2,:);
    end
end
%%
for i = fliplr(1:numFrames)
    if centroids_p1(i,:) == [0,0]
        centroids_p1(i,:) = centroids_p1(i+1,:);
    end
    if centroids_p2(i,1) < 600
        centroids_p2(i,:) = [0,0];
    end
    if centroids_p2(i,:) == [0,0]
        centroids_p2(i,:) = centroids_p2(i+1,:);
    end
end
%%
for i = 1:numFrames
    %locate disc
    frame = rgb2gray(im2double(read(vidobj,i))); %read in each frame
    if i == 135
        fig=imshow(frame)
        saveas(fig,'frame_no_adapthisteq.png');
    end
    frame = adapthisteq(frame,'NumTiles',[16 16]);
    if i == 135
        fig=imshow(frame)
        saveas(fig,'frame_adapthisteq.png');
    end
    bw = im2bw(frame,0.97);
    frame_new1 = imerode(bw,SE);
    frame_new1 = imdilate(frame_new1,SE);
    frame_new2 = imerode(bw,SE2);
    frame_new2 = imdilate(frame_new2,SE);
    frame_new3 = imerode(bw,SE3);
    frame_new3 = imdilate(frame_new3,SE);
    frame_new = frame_new1 | frame_new2 | frame_new3;
    frame_new(1:90,:) = 0;
    final_im = final_im | frame_new;
    disc_locations(:,:,i) = frame_new;
    Ilabel = bwlabel(frame_new);
    stat = regionprops(Ilabel,'centroid');
    if size(stat) == [1 1]
        cent = stat.Centroid(1:2);
        if cent(1) <= 110
            continue
        end
        centroids_disc(i,:) = cent;
    elseif size(stat) ~= [0 1]
        cents = cat(1,stat.Centroid);
        correct_reg = 1;
        for j = 1:size(cents,1)
            cent = cents(j,:);
            if cent(1) > 110
                correct_reg = j;
                break
            end
        end
        centroids_disc(i,:) = cents(correct_reg,:);
    end
end

figure,imshow(final_im)
imwrite(final_im,'2person_morphological_testpass.jpg');

%%
vidwriter = VideoWriter('2person_path.avi');
vidwriter.FrameRate = vidobj.FrameRate;
open(vidwriter);
for i = 1:numFrames
    writeVideo(vidwriter,disc_locations(:,:,i));
end
close(vidwriter);

%%
%write binary player outlines
vidwriter_player = VideoWriter('2person_player_tracking.avi');
vidwriter_player.FrameRate = vidobj.FrameRate;
open(vidwriter_player);
for i = 1:numFrames
    writeVideo(vidwriter_player,player_locations(:,:,i));
end
close(vidwriter_player);

%%
%overlay boxes on input video of player locations
vidwriter_boxes = VideoWriter('2person_player_avi');
vidwriter_boxes.FrameRate = vidobj.FrameRate;
open(vidwriter_boxes);
for i = 1:numFrames
    frame_col = read(vidobj,i);
    if centroids_p1(i,1) == 0 || centroids_p2(i,1) == 0
        writeVideo(vidwriter_boxes,frame_col);
        continue
    end
    pos_p1 = [centroids_p1(i,1)-75 centroids_p1(i,2)-100 150 300];
    pos_p2 = [centroids_p2(i,1)-75 centroids_p2(i,2)-100 150 300];
    pos = vertcat(pos_p1,pos_p2);
    frame_withrecs = insertShape(frame_col,'Rectangle',pos,'Color','yellow','LineWidth',5);
    writeVideo(vidwriter_boxes,frame_withrecs);
end
close(vidwriter_boxes);

%%
fig = figure,plot(centroids_disc(:,1)',centroids_disc(:,2)','*')
set(gca, 'YDir','reverse')
xlim([2 1280])
ylim([2 720])
title('Frisbee Centroids')
saveas(fig,'frisbee_centoids.png')

%%
fig2 = figure,plot(centroids_p1(:,1)',centroids_p1(:,2)','b*',...
    centroids_p2(:,1)',centroids_p2(:,2)','r*','MarkerSize',5)
set(gca, 'YDir','reverse')
xlim([2 1280])
ylim([2 720])
title('Player Centroids')
saveas(fig2,'player_centoids.png')

%%
%linear interpolation for pass counting/remove outliers/false positives
for i = 1:numFrames
    if centroids_disc(i,2) < 98
        centroids_disc(i,:) = [0,0];
    end
end
for i = 2:numFrames-1
    if centroids_disc(i-1,:) ~= [0,0] & centroids_disc(i+1,:) ~= [0,0] ...
            & centroids_disc(i,:) == [0,0]
        centroids_disc(i,:) = [mean([centroids_disc(i-1,1),...
            centroids_disc(i+1,1)]),mean([centroids_disc(i-1,2),...
            centroids_disc(i+1,2)])]
    end
end
for i = 2:numFrames-1
    if centroids_disc(i-1,:) == [0,0] & (centroids_disc(i+1,:) == [0,0] ...
            | centroids_disc(i+2,:) == [0,0]) & centroids_disc(i,:) ~= ...
            [0,0]
        centroids_disc(i,:) = [0,0];
    end
end


%%
%player region size determined empirically to account for windups
[num_passes,player_regions] = count_passes(centroids_disc,centroids_p1,centroids_p2,...
    5,numFrames,250,360);

%%
vidwriter_player_reg = VideoWriter('2person_player_regions.avi');
vidwriter_player_reg.FrameRate = vidobj.FrameRate;
open(vidwriter_player_reg);
for i = 1:numFrames
    writeVideo(vidwriter_player_reg,player_regions(:,:,i));
end
close(vidwriter_player_reg);
sprintf('num_pass: %d', num_passes);
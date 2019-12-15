clear, close all
vidobj = VideoReader('game_footage.mp4');
numFrames = get(vidobj,'NumberOfFrames');
%%
SE = strel('rectangle',[2 15]);
SE2 = strel('line',15,10);
SE3 = strel('line',15,20);
centroids_disc = zeros(numFrames,2);
disc_locations = zeros(720,1280,numFrames);
%team one and two player locations
t1_player_locations = zeros(size(disc_locations));
t2_player_locations = zeros(size(disc_locations));
%image which shows path of disc over course of video
final_im = zeros(720,1280);
final_im = logical(final_im);
%team1 color limits (blue team)
t1_r_lim = [0 130];
t1_g_lim = [0 130];
t1_b_lim = [70 255];
%team2 color limits (yellow team)
t2_r_lim = [200 255];
t2_g_lim = [120 255];
t2_b_lim = [0 150];

%%
centroids_t1 = zeros(20,2,numFrames);
centroids_t2 = zeros(10,2,numFrames);
num_players_t1 = zeros(numFrames,1);
num_players_t2 = zeros(numFrames,1);
for i = 1:numFrames
    %locate players using locate_players function
    frame_col = read(vidobj,i);
    t1_player_locations(:,:,i) = locate_players(frame_col,t1_r_lim,t1_g_lim,t1_b_lim,150);
    t2_player_locations(:,:,i) = locate_players(frame_col,t2_r_lim,t2_g_lim,t2_b_lim,150);
    t1_player_locations(1:200,:,i) = 0;
    t2_player_locations(1:200,:,i) = 0;
    Ilabel = bwlabel(t1_player_locations(:,:,i));
    stat = regionprops(Ilabel,'centroid');
    cents = cat(1,stat.Centroid);
    num_players_t1(i) = size(cents,1);
    centroids_t1(1:size(cents,1),:,i) = cents;
    
    Ilabel = bwlabel(t2_player_locations(:,:,i));
    stat = regionprops(Ilabel,'centroid');
    cents = cat(1,stat.Centroid);
    num_players_t2(i) = size(cents,1);
    centroids_t2(1:size(cents,1),:,i) = cents;
end
%%
%write binary player outlines
vidwriter_t1 = VideoWriter('t1_player_tracking_bin.avi');
vidwriter_t1.FrameRate = vidobj.FrameRate;
vidwriter_t2 = VideoWriter('t2_player_tracking_bin.avi');
vidwriter_t2.FrameRate = vidobj.FrameRate;
open(vidwriter_t1);
open(vidwriter_t2);
for i = 1:numFrames
    writeVideo(vidwriter_t1,t1_player_locations(:,:,i));
    writeVideo(vidwriter_t2,t2_player_locations(:,:,i));
end
close(vidwriter_t1);
close(vidwriter_t2);

%%
%overlay boxes on input video of player locations: team 1
vidwriter_boxes_t1 = VideoWriter('t1_player_boxes.avi');
vidwriter_boxes_t1.FrameRate = vidobj.FrameRate;
open(vidwriter_boxes_t1);
for i = 1:numFrames
    frame_col = read(vidobj,i);
    if centroids_t1(1,1,i) == 0
        writeVideo(vidwriter_boxes_t1,frame_col);
        continue
    end
    if num_players_t1(i) > 0
        %for each player region
        for r = 1:num_players_t1(i)
            if r == 1
                pos = [centroids_t1(r,1,i)-30 centroids_t1(r,2,i)-50 60 120];
            else
                pos = [pos; centroids_t1(r,1,i)-30 centroids_t1(r,2,i)-50 60 120];
            end
        end
    end
    frame_withrecs = insertShape(frame_col,'Rectangle',pos,'Color','red','LineWidth',5);
    writeVideo(vidwriter_boxes_t1,frame_withrecs);
end
close(vidwriter_boxes_t1);

%%
%overlay boxes on input video of player locations: team 2
vidwriter_boxes_t2 = VideoWriter('t2_player_boxes.avi');
vidwriter_boxes_t2.FrameRate = vidobj.FrameRate;
open(vidwriter_boxes_t2);
for i = 1:numFrames
    frame_col = read(vidobj,i);
    if centroids_t1(1,1,i) == 0
        writeVideo(vidwriter_boxes_t2,frame_col);
        continue
    end
    if num_players_t2(i) > 0
        %for each player region
        for r = 1:num_players_t2(i)
            if r == 1
                pos = [centroids_t2(r,1,i)-30 centroids_t2(r,2,i)-50 60 120];
            else
                pos = [pos; centroids_t2(r,1,i)-30 centroids_t2(r,2,i)-50 60 120];
            end
        end
    end
    frame_withrecs = insertShape(frame_col,'Rectangle',pos,'Color','blue','LineWidth',5);
    writeVideo(vidwriter_boxes_t2,frame_withrecs);
end
close(vidwriter_boxes_t2);

%%
for i = 1:numFrames
    %locate disc
    frame = rgb2gray(im2double(read(vidobj,i))); %read in each frame
    bw = im2bw(frame,0.95);
    if i == 280
        figure,imshow(frame)
        figure,imshow(bw)
    end
    frame_new1 = imerode(bw,SE);
    frame_new1 = imdilate(frame_new1,SE);
    frame_new2 = imerode(bw,SE2);
    frame_new2 = imdilate(frame_new2,SE);
    frame_new3 = imerode(bw,SE3);
    frame_new3 = imdilate(frame_new3,SE);
    frame_new = frame_new1 | frame_new2 | frame_new3;
    frame_new(1:200,:) = 0;
    final_im = final_im | frame_new;
    disc_locations(:,:,i) = frame_new;
end
imwrite(final_im,'game_footage_morphological_disctrack.jpg');

%%
vidwriter_disc = VideoWriter('game_footage_disc_path.avi');
vidwriter_disc.FrameRate = vidobj.FrameRate;
open(vidwriter_disc);
for i = 1:numFrames
    writeVideo(vidwriter_disc,disc_locations(:,:,i));
end
close(vidwriter_disc);

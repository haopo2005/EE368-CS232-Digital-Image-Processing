function rotate_img = preprocess(img)

figure
imshow(img);
img_gray = im2double(rgb2gray(img));
[height, width] = size(img_gray);

% binarize img
otsuLevel = graythresh(uint8(255*img_gray));
bw_img = img_gray < otsuLevel;

edges = edge(bw_img, 'sobel');
imshow(edges);
[H,T,R] = hough(edges,'RhoResolution',1,'ThetaResolution', 0.1 );
P = houghpeaks(H,6);

orientations = P(:,2);

if length(unique(orientations)) > 3    
    temp_orientation = orientations(1);
    rotate_angle = 0;
else
    temp_orientation = mode(P(:,2));
    rotate_angle = T(temp_orientation)-90;
end

rotate_angle = T(temp_orientation)-90;

while(abs(rotate_angle) > 45)
    rotate_angle = rotate_angle - sign(rotate_angle)*90;
end

angle = rotate_angle;
disp('rotate_angle:'+string(angle));

rotate_img = imrotate(img,rotate_angle,'bilinear');
figure;
imshow(rotate_img);
end


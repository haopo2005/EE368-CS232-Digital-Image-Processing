function player_mask = locate_players(frame,r_lim,g_lim,b_lim,region_lim)
%finds centroids of player locations in a given frame
player_mask = zeros(size(frame,1),size(frame,2));
r = frame(:,:,1);  %assigns first color layer to variable r
g = frame(:,:,2);  %assigns second color layer to variable g
b = frame(:,:,3);  %assigns third color layer to variable b

for i = 1:size(frame,1)
    for j = 1:size(frame,2)
        if r(i,j) <= r_lim(2) && r(i,j) >= r_lim(1) && ...
                g(i,j) <= g_lim(2) && g(i,j) >= g_lim(1) && ...
                b(i,j) <= b_lim(2) && b(i,j) >= b_lim(1)
            player_mask(i,j) = 1;
        end
    end
end
player_mask = bwareaopen(player_mask,region_lim);
end
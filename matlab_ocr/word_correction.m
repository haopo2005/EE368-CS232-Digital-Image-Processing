function  txt_corrected =  word_correction(words)
    input_file = fopen('input.txt', 'w');
    fprintf(input_file, words);
    fclose(input_file);
    system('python main.py input.txt');
%     output_file = fopen('output.txt', 'r');
    txt_corrected = fileread('output.txt');
%     fclose(output_file);
end
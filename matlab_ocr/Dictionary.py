import re

from Letters import *

class Dictionary:
    words = {}

    def __init__(self, file_name, letter_operations):
        """
        :type letter_operations: Letters
        """
        self.letter_operations = letter_operations
        self.words = {}
        f = open(file_name, "r")
        lines = f.readlines()
        f.close()
        for word in lines:
            word = word.strip()
            word_len = len(word)
            word_key = word[0] + word[-1] if (word_len > 1) else word[0]
            if word_key not in self.words:
                self.words[word_key] = {}
            if word_len not in self.words[word_key]:
                self.words[word_key][word_len] = {}
            if letter_operations.normalize(word) not in self.words[word_key][word_len]:
                self.words[word_key][word_len][letter_operations.normalize(word)] = []
            self.words[word_key][word_len][letter_operations.normalize(word)].append(word)

    def dump(self, file_name):
        f = open(file_name, "w")
        for word_key in self.words:
            f.write(word_key + '\n')
            for word_len in self.words[word_key]:
                f.write(str(word_len) + '\n')
                for word_normal in self.words[word_key][word_len]:
                    f.write(word_normal + ': ' + ', '.join(self.words[word_key][word_len][word_normal]) + '\n')
            f.write('\n')
        f.close()

    def get_words(self, key, length):
        return {} if (key not in self.words or length not in self.words[key]) else self.words[key][length]

    def find_word_list(self, unknown_word, delta):
        unknown_norm = self.letter_operations.normalize(unknown_word)
        word_len = len(unknown_word)
        first_letters = self.letter_operations.nearest(unknown_word[0], delta)
        last_letters = self.letter_operations.nearest(unknown_word[-1], delta) if (word_len > 1) else ['']
        result = [unknown_word]
        min_distance = 1.1
        for first in first_letters:
            for last in last_letters:
                word_key = first + last
                words = self.get_words(word_key, word_len)
                for norm, word_list in words.iteritems():
                    distance = self.letter_operations.word_distance(norm, unknown_norm)
                    distance += self.letter_operations.distance(unknown_word[0], first)
                    if word_len > 1:
                        distance += self.letter_operations.distance(unknown_word[-1], last)
                        distance /= 3
                    else:
                        distance /= 2
                    if distance < min_distance:
                        min_distance = distance
                        result = word_list
                    elif distance == min_distance:
                        result += word_list
        return result

    def transform(self, input_file_name, delta = 0.0, output_file_name = 'output.txt'):
        in_f = open(input_file_name, "r")
        out_f = open(output_file_name, "w")
        lines = in_f.readlines()

        first_line = True
        for line in lines:
            if not first_line:
                out_f.write('\n')
            first_line = False
            first = True
            for word in line.split():
                word = word.lower()
                data = re.findall("[\w']+|[.,!?;]", word)
                word = data.pop(0)
                found_words = self.find_word_list(word, delta)
                if not first:
                    out_f.write(' ')
                if len(found_words) == 1:
                    out_f.write(found_words[0])
                else:
                    out_f.write('[' + ', '.join(found_words) + ']')
                out_f.write(''.join(data))
                first = False

        in_f.close()
        out_f.close()

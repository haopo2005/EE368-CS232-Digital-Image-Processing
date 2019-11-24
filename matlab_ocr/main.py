from Dictionary import *
from EnglishQwertyLetters import *
import sys

letter_operations = EnglishQwertyLetters()
dictionary = Dictionary('corncob_lowercase.txt', letter_operations)
dictionary.transform(sys.argv[1], 0.3)

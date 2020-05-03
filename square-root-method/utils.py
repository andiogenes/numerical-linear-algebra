# import numpy as np
#
#
# def is_zero(v):
#     return abs(v) < np.finfo(float).eps

# TODO: заменить на рабочий builtin
def sign(x):
    return 1.0 if x > 0 else -1.0 if x < 0 else 0.0

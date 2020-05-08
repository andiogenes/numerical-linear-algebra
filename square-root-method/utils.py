import numpy as np


def is_zero(v):
    return abs(v) < np.finfo(float).eps


def sign(x):
    if is_zero(x):
        return 0

    return 1 if x > 0 else -1

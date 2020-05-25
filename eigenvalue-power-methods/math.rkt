#lang racket

(require math/matrix)
(require math/array)

(provide (all-defined-out))

(define (rand n)
  (- (* (random) n) (/ n 2)))

(define (rand-uniform) (rand 100))

(define (norm m) (sqrt (array-all-sum (matrix-map sqr m))))

(define (dot-prod l r) (array-all-sum (matrix-map * l r)))
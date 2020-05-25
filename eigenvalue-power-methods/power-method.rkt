#lang racket

(require math/matrix)
(require "math.rkt")

(provide power-iteration)

(define (power-iteration A eps)
  (let* ([A-size (matrix-num-cols A)]
         [b_k (->col-matrix (for/list
           ([i (in-range A-size)]) 
             (rand-uniform)))]
         [b_k+1 b_k]
         [l_max +inf.0]
         [l_prev 0])

    (for ([i (in-naturals)])
          #:break (< (abs (- l_max l_prev)) eps)
      (let ([tmp b_k+1]
            [tmp_norm (norm b_k+1)])
        (set! b_k+1 (matrix* A b_k))
        (set! b_k (matrix-scale tmp (/ 1 tmp_norm)))
        (set! l_prev l_max)
        (set! l_max (/ (dot-prod b_k+1 b_k) (dot-prod b_k b_k)))))

    l_max))
#lang racket

(require math/matrix)
(require "math.rkt")

(provide inverse-power-iteration)

(define (inverse-power-iteration A initial approx eps)
  (let* ([E (identity-matrix (square-matrix-size A))]
         [B (matrix-inverse (matrix- A (matrix-scale E approx)))]
         [x_k (->col-matrix initial)]
         [x_k+1 x_k]
         [l +inf.0]
         [l_prev 0]
         [iterations 0])
         
    (for ([i (in-naturals)])
          #:break (< (abs (- l l_prev)) eps)
        (let ([tmp x_k+1]
              [tmp_norm (norm x_k+1)])
          (set! x_k (matrix-scale tmp (/ 1 tmp_norm)))
          (set! x_k+1 (matrix* B x_k))
          (set! l_prev l)
          (set! l (+ approx (/ (dot-prod x_k x_k) (dot-prod x_k+1 x_k))))
          (set! iterations (add1 iterations))))

        (values l x_k iterations)))
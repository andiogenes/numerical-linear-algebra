#lang racket

(require math/matrix)
(require "power-method.rkt")

(provide max-eigenvalue)
(provide min-eigenvalue)

(define (max-eigenvalue A initial eps c)
  (some-eigenvalue A initial eps c matrix+ -))

(define (min-eigenvalue A initial eps c)
  (some-eigenvalue A initial eps c matrix- +))

(define-syntax-rule (some-eigenvalue A initial eps c matrix-op num-op)
  (let* ([E (identity-matrix (square-matrix-size A))]
         [B (matrix-op A (matrix-scale E c))])
    (let-values ([(l b iterations) (power-iteration B initial eps)])
      (values (num-op l c) b iterations))))
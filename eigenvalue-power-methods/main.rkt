#lang racket

(require yaml)
(require math/matrix)
(require "power-method.rkt")

(define source (make-parameter "assignment.yml"))
(define dest (make-parameter "destination.yml"))

(define parser
  (command-line
   #:once-each
   [("-s" "--source") SOURCE
    "Path to your assignment file"
    (source SOURCE)]
   [("-d" "--destination") DESTINATION
    "Output destination"
    (dest DESTINATION)]

   #:args () (void)))

(define (process-eigenvalues)
  (let* ([config-get ((curry hash-ref) (with-input-from-file (source) read-yaml))]
         [A (list*->matrix (config-get "matrix" '()))]
         [eps (config-get "eps" 0.1)]
         [approx (config-get "approx" 0)]
         [initial (config-get "init" (λ () (build-list (length A) 0)))])
    (power-iteration A initial eps)))


(let-values ([(l_max b_k iterations) (process-eigenvalues)])
  (printf "eigenvalue: ~s\n" l_max)
  (printf "eigenvector: ~v\n" b_k)
  (printf "iterations: ~s\n" iterations))
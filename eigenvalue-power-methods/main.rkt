#lang racket

(require yaml)
(require math/matrix)
(require "applications.rkt")
(require "power-method.rkt")
(require "inverse-power-method.rkt")

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

(define-syntax-rule (print-eigenvalues purpose v) 
  (let-values ([(l b iterations) v])
    (display (string-append purpose ":\n"))
    (printf "\teigenvalue: ~s\n" l)
    (printf "\teigenvector: ~v\n" b)
    (printf "\titerations: ~s\n" iterations)))

(define (process-eigenvalues)
  (let* ([config-get ((curry hash-ref) (with-input-from-file (source) read-yaml))]
         [A (list*->matrix (config-get "matrix" '()))]
         [eps (config-get "eps" 0.1)]
         [approx (config-get "approx" 0)]
         [initial (config-get "init" (λ () (build-list (length A) 0)))])
    (print-eigenvalues "λ_1" (power-iteration A initial eps))
    (print-eigenvalues "λ_2" (power-iteration-second A initial eps))
    (print-eigenvalues "λ_2 (метод исчерпывания)" (power-iteration-exhausting A initial eps))
    (print-eigenvalues "nearest λ" (inverse-power-iteration A initial approx eps))
    (print-eigenvalues "min λ" (min-eigenvalue A initial eps 10))
    (print-eigenvalues "max λ" (max-eigenvalue A initial eps 10))))

(process-eigenvalues)
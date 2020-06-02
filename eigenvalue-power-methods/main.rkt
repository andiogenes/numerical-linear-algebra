#lang racket

(require yaml)
(require math/matrix)
(require "applications.rkt")
(require "power-method.rkt")
(require "inverse-power-method.rkt")

(define source (make-parameter "assignment.yml"))
(define dest (make-parameter "destination.yml"))

;; Разбор аргументов командной строки
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

;; Печать информации о собственных числах
(define-syntax-rule (print-eigenvalues purpose v) 
  (let-values ([(l b iterations) v])
    (display (string-append purpose ":\n"))
    (printf "\teigenvalue: ~s\n" l)
    (printf "\teigenvector: ~v\n" b)
    (printf "\titerations: ~s\n" iterations)))

;; Обработка исходных данных
(define (process-eigenvalues)
  (let* ([config-get ((curry hash-ref) (with-input-from-file (source) read-yaml))]
         [A (list*->matrix (config-get "matrix" '()))]
         [eps (config-get "eps" 0.1)]
         [approx (config-get "approx" 0)]
         [initial (config-get "init" (λ () (build-list (length A) 0)))])
    (print-eigenvalues "λ1" (power-iteration A initial eps))
    (print-eigenvalues "λ2" (power-iteration-second A initial eps))
    (print-eigenvalues "λ2 (метод исчерпывания)" (power-iteration-exhausting A initial eps))
    (print-eigenvalues "ближайшее к λ0" (inverse-power-iteration A initial approx eps))
    (print-eigenvalues "минимальное λ" (min-eigenvalue A initial eps 10))
    (print-eigenvalues "максимальное λ" (max-eigenvalue A initial eps 10))
    (print-eigenvalues "λk" (min-absolute-eigenvalue A initial eps))))

(process-eigenvalues)
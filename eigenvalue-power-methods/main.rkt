#lang racket

(require yaml)

(define source (make-parameter "assignment.yml"))
(define dest (make-parameter "destination.yml"))

(define parser
  (command-line
   #:usage-help
   "Have the computer greet you!"

   #:once-each
   [("-s" "--source") SOURCE
    "Path to your assignment file"
    (source SOURCE)]
   [("-d" "--destination") DESTINATION
    "Output destination"
    (dest DESTINATION)]

   #:args () (void)))

(define process-eigenvalues
  (let* ([config-get ((curry hash-ref) (with-input-from-file (source) read-yaml))]
         [matrix (config-get "matrix" '())]
         [eps (config-get "eps" 0.1)]
         [approx (config-get "approx" 0)]
         [initial (config-get "init" (λ () (build-list (length matrix) 0)))])
    (thunk (print matrix))))


(process-eigenvalues)
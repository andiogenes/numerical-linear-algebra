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
  (let* ([config (with-input-from-file (source) read-yaml)]
         [matrix (hash-ref config "matrix" '())]
         [eps (hash-ref config "eps" 0.1)]
         [approx (hash-ref config "approx" 0)]
         [initial (hash-ref config "init" (λ () (build-list (length matrix) 0)))])
    (thunk (print config))))
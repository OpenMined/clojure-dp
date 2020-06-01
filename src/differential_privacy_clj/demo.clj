(ns differential-privacy-clj.demo
  (:require [differential-privacy-clj.core :as dp]))


(def example-data [1.0 2.0 13.0 3.5 1.0 10.0 9.0])

(def true-sum (reduce + example-data))
(def true-count (count example-data))

(def private-sum (dp/bounded-sum example-data
                                 :lower 0.0
                                 :upper 15.0
                                 :max-partitions 1
                                 :epsilon 1.0))

(def private-count (dp/count example-data
                             :max-partitions 1
                             :epsilon 1.0))

(println)
(println "True sum:\t" true-sum)
(println "Private sum:\t" private-sum)
(println)
(println "True count:\t" true-count)
(println "Private count:\t" private-count)

;; This also works:
#_(println "Gaussian noise:\t" (dp/gaussian-noise))
#_(println "Laplacian noise:\t" (dp/laplace-noise))

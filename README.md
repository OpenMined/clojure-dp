# differential-privacy-clj

A Clojure wrapper for the Java [differential-privacy library](https://github.com/google/differential-privacy).

See [demo notebooks](https://github.com/OpenMined/org.openmined.dp/doc/clojure/).

## Usage

Steps to make it work:

1. Install [Bazel](https://docs.bazel.build/versions/master/install.html) if you
don't have it already.
2. Download and build the Java [differential-privacy library](https://github.com/google/differential-privacy):
```
git clone https://github.com/google/differential-privacy.git
cd java
bazel build ...
```
3. Copy `libdifferentialprivacy.jar` to your local Maven repository and
rename it to `libdifferentialprivacy-1.0.jar`:
```
mkdir -p ~/.m2/repository/com/google/privacy/differentialprivacy/libdifferentialprivacy/1.0/
cp bazel-bin/main/com/google/privacy/differentialprivacy/libdifferentialprivacy.jar ~/.m2/repository/com/google/privacy/differentialprivacy/libdifferentialprivacy/1.0/libdifferentialprivacy-1.0.jar
```

4. Make sure you have [leiningen](https://leiningen.org/) installed.
5. Install the library (`lein install`) or test it in `lein repl`
([notebooks are also available](https://github.com/OpenMined/org.openmined.dp/doc/clojure/)).

Example `lein repl` session:
```clojure
differential-privacy-clj.core=> (load "demo")  ;; see src//differential_privacy_clj/demo.clj

True sum:	 39.5
Private sum:	 41.26479895079683

True count:	 7
Private count:	 7
nil
differential-privacy-clj.core=> ;; DP sum of 100 random numbers between 0.0 and 10.0:
differential-privacy-clj.core=> (def random-numbers (take 100 (repeatedly #(rand 10.0))))
#'differential-privacy-clj.core/random-numbers
differential-privacy-clj.core=> (bounded-sum random-numbers :lower 0 :upper 10 :max-partitions 1 :epsilon 1)
494.8682999070588
differential-privacy-clj.core=>
```

Installation steps 1-3 above will not be necessary once `differential-privacy` library is released and available from Maven Central.

Installation steps 4-5 will not be necessary when differential-privacy-clj is released shortly after differential-privacy.

## TODO:

* Add tests
* Add more algorithms when they are available

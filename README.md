# differential-privacy-clj

A Clojure wrapper for the Java [differential-privacy library](https://github.com/google/differential-privacy).

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
5. Build a jar with the library (`lein uberjar`) or test it in `lein repl`
(notebooks coming "soon").

Example `lein repl` session:
```clojure
differential-privacy-clj.core=> (load "demo")  ;; see src//differential_privacy_clj/demo.clj

Private sum:	 370.53845703287516
Private count:	 4
nil
differential-privacy-clj.core=> (def my-sum (bounded-sum :lower 0.0 :upper 10.0 :max-partitions 12 :epsilon 1.0))
#'differential-privacy-clj.core/my-sum
differential-privacy-clj.core=> (compute! my-sum [1.0 2.0 3.0 5.0 8.0])
101.33194517646916
differential-privacy-clj.core=> (compute! my-sum)
IllegalStateException The result can be calculated and returned only once.  com.google.privacy.differentialprivacy.BoundedSum.computeResult (BoundedSum.java:106)
```


Steps 1-3 above will not be necessary once `differential-privacy` library is released and available from Maven Central.

## TODO:

1. Create notebooks.
2. Add more functions when they become available.
3. Carrots demo.

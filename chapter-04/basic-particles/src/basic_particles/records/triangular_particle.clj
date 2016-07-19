(ns basic-particles.records.triangular-particle
  (:require [quil.core :as q]
            [basic-particles.protocols.particle :refer :all]
            [basic-particles.protocols.vector :refer :all]))

(defrecord TriangularParticle [location velocity lifespan angle speed mass]
  
  Particle
  
  (step [p [& forces]]
  	(let [new-lifespan (- lifespan (* 2 speed))
          acceleration (divide (multiply (reduce add forces) speed) mass)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)
          angular-velocity (/ (:x new-velocity) 10)
          new-angle (+ angle angular-velocity)]
     (TriangularParticle. new-location new-velocity new-lifespan new-angle speed mass)))
  
  (render [p]
  	(q/stroke 0 lifespan)
    (q/rect-mode :center)
   	(q/fill 175 lifespan)
    (q/with-translation [(:x location) (:y location)]
      (q/with-rotation [angle]
        (q/triangle -2.5 0 0 5 2.5 0))))
  
  (is-alive? [p]
  	(> lifespan 0)))
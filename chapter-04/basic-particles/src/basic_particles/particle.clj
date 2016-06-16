(ns basic-particles.particle
  (:require [quil.core :as q]
            [basic-particles.vector :as v]
            [basic-particles.protocols.particle-operations :refer :all]
            [basic-particles.protocols.vector-operations :refer :all])
  (:import [basic_particles.vector Vector]))

(defrecord Particle [location velocity acceleration lifespan]
  
  ParticleOperations
  
  (update-particle [p]
  	(let [new-lifespan (- lifespan 2)
          new-velocity (add velocity acceleration)
          new-location (add location new-velocity)]
     (Particle. new-location new-velocity acceleration new-lifespan)))
  
  (render [p]
  	(q/stroke 0 lifespan)
   	(q/fill 175 lifespan)
    (q/ellipse (:x location) (:y location) 8 8))
  
  (is-alive? [p]
  	(> lifespan 0)))
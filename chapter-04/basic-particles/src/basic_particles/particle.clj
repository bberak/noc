(ns basic-particles.particle
  (:require [quil.core :as q]
            [basic-particles.vector :as v])
  (:import [basic_particles.vector Vector]))

(defrecord Particle [location velocity acceleration lifespan])
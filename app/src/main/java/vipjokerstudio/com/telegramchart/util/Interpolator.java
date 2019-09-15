package vipjokerstudio.com.telegramchart.util;


public interface Interpolator {
    Interpolator LINEAR = Mathf::lerp;

    Interpolator EASE_OUT_QUAD_OPT = (start, diff, ratioPassed) ->
            -diff * ratioPassed * (ratioPassed - 2) + start;

    Interpolator EASE_IN_QUAD_OPT = (start, diff, ratioPassed) ->
            diff * ratioPassed * ratioPassed + start;

    Interpolator EASE_IN_OUT_QUAD_OPT = (start, diff, ratioPassed) -> {
        ratioPassed /= .5f;
        if (ratioPassed < 1) return diff / 2 * ratioPassed * ratioPassed + start;
        ratioPassed--;
        return -diff / 2 * (ratioPassed * (ratioPassed - 2) - 1) + start;
    };

    Interpolator CLERP = (start, end, val) -> {
        float min = 0.0f;
        float max = 360.0f;
        float half = Mathf.abs((max - min) / 2.0f);
        float retval = 0.0f;
        float diff = 0.0f;
        if ((end - start) < -half) {
            diff = ((max - start) + end) * val;
            retval = start + diff;
        } else if ((end - start) > half) {
            diff = -((max - end) + start) * val;
            retval = start + diff;
        } else retval = start + (end - start) * val;
        return retval;
    };

    Interpolator SPRING = (start, end, val) -> {
        val = Mathf.clamp01(val);
        val = (Mathf.sin(val * Mathf.PI * (0.2f + 2.5f * val * val * val)) * Mathf.pow(1f - val, 2.2f) + val) * (1f + (1.2f * (1f - val)));
        return start + (end - start) * val;
    };

    Interpolator EASEINQUAD = (start, end, val) -> {
        end -= start;
        return end * val * val + start;
    };

    Interpolator EASEOUTQUAD = (start, end, val) -> {
        end -= start;
        return -end * val * (val - 2) + start;
    };

    Interpolator EASEINOUTQUAD = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return end / 2 * val * val + start;
        val--;
        return -end / 2 * (val * (val - 2) - 1) + start;
    };

    Interpolator EASEINCUBIC = (start, end, val) -> {
        end -= start;
        return end * val * val * val + start;
    };

    Interpolator EASEOUTCUBIC = (start, end, val) -> {
        val--;
        end -= start;
        return end * (val * val * val + 1) + start;
    };

    Interpolator EASEINOUTCUBIC = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return end / 2 * val * val * val + start;
        val -= 2;
        return end / 2 * (val * val * val + 2) + start;
    };

    Interpolator EASEINQUART = (start, end, val) -> {
        end -= start;
        return end * val * val * val * val + start;
    };

    Interpolator EASEOUTQUART = (start, end, val) -> {
        val--;
        end -= start;
        return -end * (val * val * val * val - 1) + start;
    };

    Interpolator EASEINOUTQUART = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return end / 2 * val * val * val * val + start;
        val -= 2;
        return -end / 2 * (val * val * val * val - 2) + start;
    };

    Interpolator EASEINQUINT = (start, end, val) -> {
        end -= start;
        return end * val * val * val * val * val + start;
    };

    Interpolator EASEOUTQUINT = (start, end, val) -> {
        val--;
        end -= start;
        return end * (val * val * val * val * val + 1) + start;
    };

    Interpolator EASEINOUTQUINT = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return end / 2 * val * val * val * val * val + start;
        val -= 2;
        return end / 2 * (val * val * val * val * val + 2) + start;
    };

    Interpolator EASEINSINE = (start, end, val) -> {
        end -= start;
        return -end * Mathf.cos(val / 1 * (Mathf.PI / 2)) + end + start;
    };

    Interpolator EASEOUTSINE = (start, end, val) -> {
        end -= start;
        return end * Mathf.sin(val / 1 * (Mathf.PI / 2)) + start;
    };

    Interpolator EASEINOUTSINE = (start, end, val) -> {
        end -= start;
        return -end / 2 * (Mathf.cos(Mathf.PI * val / 1) - 1) + start;
    };

    Interpolator EASEINEXPO = (start, end, val) -> {
        end -= start;
        return end * Mathf.pow(2, 10 * (val / 1 - 1)) + start;
    };

    Interpolator EASEOUTEXPO = (start, end, val) -> {
        end -= start;
        return end * (-Mathf.pow(2, -10 * val / 1) + 1) + start;
    };

    Interpolator EASEINOUTEXPO = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return end / 2 * Mathf.pow(2, 10 * (val - 1)) + start;
        val--;
        return end / 2 * (-Mathf.pow(2, -10 * val) + 2) + start;
    };

    Interpolator EASEINCIRC = (start, end, val) -> {
        end -= start;
        return -end * (Mathf.sqrt(1 - val * val) - 1) + start;
    };

    Interpolator EASEOUTCIRC = (start, end, val) -> {
        val--;
        end -= start;
        return end * Mathf.sqrt(1 - val * val) + start;
    };

    Interpolator EASEINOUTCIRC = (start, end, val) -> {
        val /= .5f;
        end -= start;
        if (val < 1) return -end / 2 * (Mathf.sqrt(1 - val * val) - 1) + start;
        val -= 2;
        return end / 2 * (Mathf.sqrt(1 - val * val) + 1) + start;
    };

    Interpolator EASEOUTBOUNCE = (start, end, val) -> {
        val /= 1f;
        end -= start;
        if (val < (1 / 2.75f)) {
            return end * (7.5625f * val * val) + start;
        } else if (val < (2 / 2.75f)) {
            val -= (1.5f / 2.75f);
            return end * (7.5625f * (val) * val + .75f) + start;
        } else if (val < (2.5 / 2.75)) {
            val -= (2.25f / 2.75f);
            return end * (7.5625f * (val) * val + .9375f) + start;
        } else {
            val -= (2.625f / 2.75f);
            return end * (7.5625f * (val) * val + .984375f) + start;
        }
    };

    Interpolator EASEINBOUNCE = (start, end, val) -> {
        end -= start;
        float d = 1f;
        return end - Interpolator.EASEOUTBOUNCE.update(0, end, d - val) + start;
    };

    Interpolator EASEINOUTBOUNCE = (start, end, val) -> {
        end -= start;
        float d = 1f;
        if (val < d / 2) return Interpolator.EASEINBOUNCE.update(0, end, val * 2) * 0.5f + start;
        else
            return Interpolator.EASEOUTBOUNCE.update(0, end, val * 2 - d) * 0.5f + end * 0.5f + start;
    };

    Interpolator EASEINBACK = (start, end, val) -> {
        end -= start;
        val /= 1;
        float s = 1.70158f;
        return end * (val) * val * ((s + 1) * val - s) + start;
    };

    Interpolator EASEOUTBACK = (start, end, val) -> {
        float s = 1.70158f;
        end -= start;
        val = (val / 1) - 1;
        return end * ((val) * val * ((s + 1) * val + s) + 1) + start;
    };

    Interpolator EASEINOUTBACK = (start, end, val) -> {
        float s = 1.70158f;
        end -= start;
        val /= .5f;
        if ((val) < 1) {
            s *= (1.525f);
            return end / 2 * (val * val * (((s) + 1) * val - s)) + start;
        }
        val -= 2;
        s *= (1.525f);
        return end / 2 * ((val) * val * (((s) + 1) * val + s) + 2) + start;
    };

    Interpolator EASEINELASTIC = (start, end, val) -> {
        end -= start;

        float d = 1f;
        float p = d * .3f;
        float s = 0;
        float a = 0;

        if (val == 0) return start;
        val = val / d;
        if (val == 1) return start + end;

        if (a == 0f || a < Mathf.abs(end)) {
            a = end;
            s = p / 4;
        } else {
            s = p / (2 * Mathf.PI) * Mathf.asin(end / a);
        }
        val = val - 1;
        return -(a * Mathf.pow(2, 10 * val) * Mathf.sin((val * d - s) * (2 * Mathf.PI) / p)) + start;
    };

    Interpolator EASEOUTELASTIC = (start, end, val) -> {
        /* GFX47 MOD END */
        //Thank you to rafael.marteleto for fixing this as a port over from Pedro'NODES UnityTween
        end -= start;

        float d = 1f;
        float p = d * .3f;
        float s = 0;
        float a = 0;

        if (val == 0) return start;

        val = val / d;
        if (val == 1) return start + end;

        if (a == 0f || a < Mathf.abs(end)) {
            a = end;
            s = p / 4;
        } else {
            s = p / (2 * Mathf.PI) * Mathf.asin(end / a);
        }

        return (a * Mathf.pow(2, -10 * val) * Mathf.sin((val * d - s) * (2 * Mathf.PI) / p) + end + start);
    };

    Interpolator EASEINOUTELASTIC = (start, end, val) -> {
        end -= start;

        float d = 1f;
        float p = d * .3f;
        float s = 0;
        float a = 0;

        if (val == 0) return start;

        val = val / (d / 2);
        if (val == 2) return start + end;

        if (a == 0f || a < Mathf.abs(end)) {
            a = end;
            s = p / 4;
        } else {
            s = p / (2 * Mathf.PI) * Mathf.asin(end / a);
        }

        if (val < 1) {
            val = val - 1;
            return -0.5f * (a * Mathf.pow(2, 10 * val) * Mathf.sin((val * d - s) * (2 * Mathf.PI) / p)) + start;
        }
        val = val - 1;
        return a * Mathf.pow(2, -10 * val) * Mathf.sin((val * d - s) * (2 * Mathf.PI) / p) * 0.5f + end + start;
    };


    float update(float start, float end, float value);


}



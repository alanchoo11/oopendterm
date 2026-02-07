import { motion, animate } from 'framer-motion';
import { useEffect, useState } from 'react';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

interface StatCardProps {
  title: string;
  value: number;
  suffix?: string;
  prefix?: string;
  icon: React.ElementType;
  trend?: 'up' | 'down' | 'neutral';
  trendValue?: string;
  color: 'blue' | 'green' | 'purple' | 'orange';
  delay?: number;
}

const colorMap = {
  blue: {
    bg: 'from-neon-blue/20 to-neon-blue/5',
    border: 'border-neon-blue/30',
    glow: 'shadow-glow',
    text: 'text-neon-blue',
    iconBg: 'bg-neon-blue/20',
  },
  green: {
    bg: 'from-neon-green/20 to-neon-green/5',
    border: 'border-neon-green/30',
    glow: 'shadow-glow-green',
    text: 'text-neon-green',
    iconBg: 'bg-neon-green/20',
  },
  purple: {
    bg: 'from-neon-purple/20 to-neon-purple/5',
    border: 'border-neon-purple/30',
    glow: 'shadow-glow-purple',
    text: 'text-neon-purple',
    iconBg: 'bg-neon-purple/20',
  },
  orange: {
    bg: 'from-neon-orange/20 to-neon-orange/5',
    border: 'border-neon-orange/30',
    glow: 'shadow-glow-orange',
    text: 'text-neon-orange',
    iconBg: 'bg-neon-orange/20',
  },
};

function AnimatedCounter({ value, prefix = '', suffix = '' }: { value: number; prefix?: string; suffix?: string }) {
  const [displayValue, setDisplayValue] = useState(0);
  
  useEffect(() => {
    const controls = animate(0, value, {
      duration: 2,
      ease: [0.4, 0, 0.2, 1],
      onUpdate: (latest) => {
        setDisplayValue(Math.round(latest * 100) / 100);
      },
    });
    
    return controls.stop;
  }, [value]);
  
  return (
    <span className="tabular-nums">
      {prefix}{displayValue.toLocaleString()}{suffix}
    </span>
  );
}

export function StatCard({ 
  title, 
  value, 
  suffix = '', 
  prefix = '',
  icon: Icon, 
  trend = 'neutral',
  trendValue = '0%',
  color,
  delay = 0,
}: StatCardProps) {
  const colors = colorMap[color];
  
  const TrendIcon = trend === 'up' ? TrendingUp : trend === 'down' ? TrendingDown : Minus;
  const trendColor = trend === 'up' ? 'text-neon-green' : trend === 'down' ? 'text-red-400' : 'text-white/40';
  
  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.6, delay, ease: [0.4, 0, 0.2, 1] }}
      whileHover={{ y: -4, transition: { duration: 0.2 } }}
      className={`
        relative overflow-hidden rounded-2xl p-6
        bg-gradient-to-br ${colors.bg}
        border ${colors.border}
        ${colors.glow}
        transition-all duration-300
        group cursor-pointer
      `}
    >
      {/* Background glow effect */}
      <div className={`
        absolute -top-20 -right-20 w-40 h-40 rounded-full
        bg-gradient-to-br ${colors.bg} opacity-50 blur-3xl
        group-hover:opacity-70 transition-opacity duration-500
      `} />
      
      {/* Shimmer effect */}
      <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity duration-500">
        <div className="absolute inset-0 animate-shimmer bg-gradient-to-r from-transparent via-white/5 to-transparent" />
      </div>
      
      <div className="relative">
        {/* Header */}
        <div className="flex items-start justify-between mb-4">
          <div className={`
            p-3 rounded-xl ${colors.iconBg}
            transition-transform duration-300 group-hover:scale-110
          `}>
            <Icon className={`w-6 h-6 ${colors.text}`} />
          </div>
          
          {/* Trend indicator */}
          <div className={`flex items-center gap-1 text-sm ${trendColor}`}>
            <TrendIcon className="w-4 h-4" />
            <span>{trendValue}</span>
          </div>
        </div>
        
        {/* Value */}
        <div className="mb-1">
          <span className="text-4xl font-bold text-white tracking-tight">
            <AnimatedCounter value={value} prefix={prefix} suffix={suffix} />
          </span>
        </div>
        
        {/* Title */}
        <p className="text-sm text-white/60 font-medium">{title}</p>
        
        {/* Bottom glow line */}
        <div className={`
          absolute bottom-0 left-0 right-0 h-0.5
          bg-gradient-to-r from-transparent via-${colors.text.split('-')[1]}-500 to-transparent
          opacity-0 group-hover:opacity-100 transition-opacity duration-500
        `} />
      </div>
    </motion.div>
  );
}

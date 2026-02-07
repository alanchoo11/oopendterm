import { motion } from 'framer-motion';
import { 
  Trophy, 
  Users, 
  Calendar, 
  Zap,
  Star,
  TrendingUp,
  Activity
} from 'lucide-react';
import { StatCard } from '@/components/StatCard';
import type { DashboardData } from '@/types';
import { mockData } from '@/services/api';

interface DashboardProps {
  data?: DashboardData;
}

export function Dashboard({ data = mockData }: DashboardProps) {
  const { stats, teamStats, playerStats, topPlayers, freeAgents } = data;
  
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
      className="space-y-8 p-8"
    >
      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Teams"
          value={stats.totalTeams}
          icon={Trophy}
          color="blue"
          trend="up"
          trendValue="+12%"
          delay={0}
        />
        <StatCard
          title="Total Players"
          value={stats.totalPlayers}
          icon={Users}
          color="green"
          trend="up"
          trendValue="+8%"
          delay={0.1}
        />
        <StatCard
          title="Total Matches"
          value={stats.totalMatches}
          icon={Calendar}
          color="purple"
          trend="neutral"
          trendValue="0%"
          delay={0.2}
        />
        <StatCard
          title="Tournaments"
          value={stats.totalTournaments}
          icon={Zap}
          color="orange"
          trend="up"
          trendValue="+25%"
          delay={0.3}
        />
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Top Players */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6, delay: 0.4 }}
          className="lg:col-span-2 glass-card rounded-2xl p-6"
        >
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-lg font-semibold text-white">Top Rated Players</h3>
              <p className="text-sm text-white/50">Highest performing athletes</p>
            </div>
            <div className="flex items-center gap-2 text-neon-blue">
              <Star className="w-4 h-4" />
              <span className="text-sm font-medium">Avg: {stats.averageRating.toFixed(2)}</span>
            </div>
          </div>
          
          <div className="space-y-4">
            {topPlayers.map((player, index) => (
              <motion.div
                key={player.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.4, delay: 0.5 + index * 0.1 }}
                whileHover={{ x: 4, backgroundColor: 'rgba(255,255,255,0.03)' }}
                className="flex items-center gap-4 p-4 rounded-xl bg-white/[0.02] border border-white/5 
                           transition-all duration-300 cursor-pointer group"
              >
                {/* Rank */}
                <div className={`
                  w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold
                  ${index === 0 ? 'bg-neon-orange/20 text-neon-orange' :
                    index === 1 ? 'bg-white/10 text-white/70' :
                    index === 2 ? 'bg-neon-orange/10 text-neon-orange/70' :
                    'bg-white/5 text-white/40'}
                `}>
                  {index + 1}
                </div>
                
                {/* Player Info */}
                <div className="flex-1">
                  <p className="font-medium text-white group-hover:text-neon-blue transition-colors">
                    {player.firstName} {player.lastName}
                  </p>
                  <p className="text-sm text-white/50">{player.position}</p>
                </div>
                
                {/* Rating */}
                <div className="flex items-center gap-2">
                  <div className="flex items-center gap-1">
                    <Star className="w-4 h-4 text-neon-orange fill-neon-orange" />
                    <span className="text-lg font-bold text-white">{player.rating}</span>
                  </div>
                </div>
                
                {/* Trend */}
                <TrendingUp className="w-4 h-4 text-neon-green" />
              </motion.div>
            ))}
          </div>
        </motion.div>

        {/* Side Panel */}
        <div className="space-y-6">
          {/* Player Stats */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.5 }}
            className="glass-card rounded-2xl p-6"
          >
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 rounded-lg bg-neon-purple/20">
                <Activity className="w-5 h-5 text-neon-purple" />
              </div>
              <h3 className="text-lg font-semibold text-white">Player Statistics</h3>
            </div>
            
            <div className="space-y-4">
              <div className="flex justify-between items-center">
                <span className="text-white/60">Average Age</span>
                <span className="text-white font-medium">{playerStats.averageAge} years</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-white/60">Free Agents</span>
                <span className="text-neon-orange font-medium">{playerStats.freeAgentsCount}</span>
              </div>
              <div className="flex justify-between items-center">
                <span className="text-white/60">Highest Rated</span>
                <span className="text-white font-medium">{playerStats.highestRatedPlayer}</span>
              </div>
            </div>
            
            {/* Position Distribution */}
            <div className="mt-6 pt-6 border-t border-white/5">
              <p className="text-sm text-white/50 mb-4">Positions</p>
              <div className="space-y-2">
                {Object.entries(playerStats.playersByPosition).map(([position, count]) => (
                  <div key={position} className="flex items-center gap-3">
                    <span className="text-sm text-white/60 w-20">{position}</span>
                    <div className="flex-1 h-2 bg-white/10 rounded-full overflow-hidden">
                      <motion.div
                        initial={{ width: 0 }}
                        animate={{ width: `${(count / stats.totalPlayers) * 100}%` }}
                        transition={{ duration: 1, delay: 0.8 }}
                        className="h-full bg-gradient-to-r from-neon-blue to-neon-purple rounded-full"
                      />
                    </div>
                    <span className="text-sm text-white/60 w-8 text-right">{count}</span>
                  </div>
                ))}
              </div>
            </div>
          </motion.div>

          {/* Free Agents */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.6, delay: 0.6 }}
            className="glass-card rounded-2xl p-6"
          >
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-white">Free Agents</h3>
              <span className="px-2 py-1 rounded-full bg-neon-orange/20 text-neon-orange text-xs font-medium">
                {freeAgents.length} available
              </span>
            </div>
            
            <div className="space-y-3">
              {freeAgents.map((player, index) => (
                <motion.div
                  key={player.id}
                  initial={{ opacity: 0, x: 20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.4, delay: 0.7 + index * 0.1 }}
                  className="flex items-center gap-3 p-3 rounded-lg bg-white/[0.02] border border-white/5"
                >
                  <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-neon-orange to-red-500 
                                  flex items-center justify-center text-white font-bold">
                    {player.firstName[0]}{player.lastName[0]}
                  </div>
                  <div className="flex-1">
                    <p className="text-sm font-medium text-white">{player.firstName} {player.lastName}</p>
                    <p className="text-xs text-white/50">{player.position} • {player.age} years</p>
                  </div>
                  <div className="flex items-center gap-1">
                    <Star className="w-3 h-3 text-neon-orange" />
                    <span className="text-sm font-medium text-white">{player.rating}</span>
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        </div>
      </div>

      {/* Teams by Sport */}
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, delay: 0.7 }}
        className="glass-card rounded-2xl p-6"
      >
        <h3 className="text-lg font-semibold text-white mb-6">Teams by Sport</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {Object.entries(teamStats.teamsBySport).map(([sport, count], index) => (
            <motion.div
              key={sport}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.4, delay: 0.8 + index * 0.1 }}
              whileHover={{ scale: 1.02 }}
              className="relative p-6 rounded-xl bg-white/[0.02] border border-white/5 
                         hover:border-neon-blue/30 transition-all duration-300 group"
            >
              <div className="absolute inset-0 rounded-xl bg-gradient-to-br from-neon-blue/10 to-transparent 
                              opacity-0 group-hover:opacity-100 transition-opacity" />
              <div className="relative">
                <p className="text-3xl font-bold text-white mb-1">{count}</p>
                <p className="text-white/60">{sport}</p>
                <div className="mt-4 h-1 bg-white/10 rounded-full overflow-hidden">
                  <motion.div
                    initial={{ width: 0 }}
                    animate={{ width: `${(count / stats.totalTeams) * 100}%` }}
                    transition={{ duration: 1, delay: 1 + index * 0.1 }}
                    className="h-full bg-gradient-to-r from-neon-blue to-neon-cyan rounded-full"
                  />
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </motion.div>
    </motion.div>
  );
}

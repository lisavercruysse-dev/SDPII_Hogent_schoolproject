import { TaskCards } from './TaskCards.jsx'

export function TaskList({tasks, searchQuery, setSearchQuery, onTaskDetailsClick, onCompleted, onCancel}) {
    return (
        <div>
            <TaskCards
                tasks={tasks}
                searchQuery={searchQuery}
                setSearchQuery={setSearchQuery}
                onTaskDetailsClick={onTaskDetailsClick}
                onCompleted={onCompleted}
                onCancel={onCancel}
              />
        </div>  
    )
}